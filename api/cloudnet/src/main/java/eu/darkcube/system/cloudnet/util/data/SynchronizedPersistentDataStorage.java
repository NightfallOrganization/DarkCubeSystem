/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudnet.util.data;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.cloudnet.util.data.packets.PacketNodeWrapperDataClearSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketNodeWrapperDataRemove;
import eu.darkcube.system.cloudnet.util.data.packets.PacketNodeWrapperDataSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataClearSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataRemove;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeGetOrDefault;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeQuery;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;

/**
 * A data storage that is synchronized over the entire cloud system
 */
@SuppressWarnings("InfiniteLoopStatement")
public class SynchronizedPersistentDataStorage implements PersistentDataStorage {
    private static final Map<Key, WeakStorageReference> storages = new ConcurrentHashMap<>();
    private static final ReferenceQueue<SynchronizedPersistentDataStorage> queue = new ReferenceQueue<>();

    static {
        Executors.newSingleThreadExecutor(runnable -> {
            var thread = new Thread(runnable, "SynchronizedPersistentDataStorageCleaner");
            thread.setDaemon(true);
            return thread;
        }).submit(() -> {
            while (true) {
                var ref = (WeakStorageReference) queue.remove();
                storages.remove(ref.key, ref);
            }
        });
        PacketAPI.instance().registerHandler(PacketNodeWrapperDataClearSet.class, packet -> {
            var ref = storages.get(packet.storageKey());
            if (ref == null) return null;
            var storage = ref.get();
            if (storage == null) {
                storages.remove(packet.storageKey(), ref);
                return null;
            }
            try {
                storage.lock.writeLock().lock();

                storage.clearData();
                storage.data.asMap().putAll(packet.data().asMap());
                storage.notifyNotifiers();
            } finally {
                storage.lock.writeLock().unlock();
            }

            return null;
        });
        PacketAPI.instance().registerHandler(PacketNodeWrapperDataRemove.class, packet -> {
            var ref = storages.get(packet.storageKey());
            if (ref == null) return null;
            var storage = ref.get();
            if (storage == null) {
                storages.remove(packet.storageKey(), ref);
                return null;
            }
            try {
                storage.lock.writeLock().lock();
                if (storage.data.has(packet.entryKey().toString())) {
                    storage.data.remove(packet.entryKey().toString());
                    storage.cache.remove(packet.entryKey());
                } else {
                    new IllegalStateException("Data that was not present was tried to be removed. (THIS IS KINDA ERROR, BUT NOT FATAL)").printStackTrace();
                }
                storage.notifyNotifiers();
            } finally {
                storage.lock.writeLock().unlock();
            }
            return null;
        });
        PacketAPI.instance().registerHandler(PacketNodeWrapperDataSet.class, packet -> {
            var ref = storages.get(packet.storageKey());
            if (ref == null) return null;
            var storage = ref.get();
            if (storage == null) {
                storages.remove(packet.storageKey(), ref);
                return null;
            }
            try {
                storage.lock.writeLock().lock();
                storage.data.add(packet.entryKey().toString(), packet.data());
                storage.cache.remove(packet.entryKey());
                storage.notifyNotifiers();
            } finally {
                storage.lock.writeLock().unlock();
            }
            return null;
        });
    }

    private final String table;
    private final Key key;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private final JsonObject data = new JsonObject();

    public SynchronizedPersistentDataStorage(String table, Key key) {
        this.table = table;
        this.key = key;
        this.data.asMap().putAll(new PacketWrapperNodeQuery(table, key).sendQuery(PacketWrapperNodeQuery.Response.class).data().asMap());
        storages.put(key, new WeakStorageReference(this));
    }

    private static class WeakStorageReference extends WeakReference<SynchronizedPersistentDataStorage> {
        private final Key key;

        public WeakStorageReference(SynchronizedPersistentDataStorage referent) {
            super(referent);
            this.key = referent.key;
        }
    }

    @Override
    public @UnmodifiableView @NotNull PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Override
    public @NotNull Collection<Key> keys() {
        List<Key> keys = new ArrayList<>();
        try {
            lock.readLock().lock();
            for (var s : data.keySet()) {
                if (s.contains(":")) {
                    keys.add(Key.key(s));
                } else {
                    keys.add(Key.key("", s));
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return Collections.unmodifiableCollection(keys);
    }

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        var json = type.serialize(data);
        var result = new PacketWrapperNodeDataSet(table, this.key, key, json).sendQuery(PacketWrapperNodeDataSet.Result.class);
        if (!result.confirmed()) {
            throw new RuntimeException("Failed to set data for DataStorage " + this.key + " at key " + key);
        }
    }

    @Override
    public void remove(@NotNull Key key) {
        new PacketWrapperNodeDataRemove(table, this.key, key).sendQuery(PacketWrapperNodeDataRemove.Result.class);
    }

    @Override
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        var confirmation = new PacketWrapperNodeDataRemove(table, this.key, key).sendQuery(PacketWrapperNodeDataRemove.Result.class);
        if (confirmation.removed() == null) {
            return null;
        }
        return type.deserialize(confirmation.removed());
    }

    @Override
    public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            lock.readLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
            if (!data.has(key.toString())) {
                return null;
            }
            var value = type.clone(type.deserialize(data.get(key.toString())));
            cache.put(key, value);
            return type.clone(value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public @NotNull <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
        try {
            lock.readLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        var data = defaultValue.get();
        var json = type.serialize(data);
        var result = new PacketWrapperNodeGetOrDefault(table, this.key, key, json).sendQuery(PacketWrapperNodeGetOrDefault.Result.class);
        var parsed = type.deserialize(result.data());
        try {
            lock.writeLock().lock();
            var old = (T) cache.putIfAbsent(key, parsed);
            if (old == null) {
                return type.clone(parsed);
            }
            return type.clone(old);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        get(key, type, () -> data);
    }

    @Override
    public boolean has(@NotNull Key key) {
        try {
            lock.readLock().lock();
            return data.has(key.toString());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        loadFromJsonObject(new JsonObject());
    }

    private void clearData() {
        cache.clear();
        for (var s : new ArrayList<>(data.keySet())) {
            data.remove(s);
        }
    }

    @Override
    public void loadFromJsonObject(@NotNull JsonObject json) {
        new PacketWrapperNodeDataClearSet(table, key, json).sendQuery(PacketWrapperNodeDataClearSet.Result.class);
    }

    @Override
    public @NotNull JsonObject storeToJsonObject() {
        try {
            lock.readLock().lock();
            return data.deepCopy();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return Collections.unmodifiableCollection(updateNotifiers);
    }

    @Override
    public void clearCache() {
        try {
            lock.writeLock().lock();
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.add(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.remove(notifier);
    }

    public Key key() {
        return key;
    }

    private void notifyNotifiers() {
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
