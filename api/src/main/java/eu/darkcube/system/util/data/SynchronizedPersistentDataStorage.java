/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.util.data.packets.PacketData;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataClearSet;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataRemove;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataSet;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataClearSet;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataRemove;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataSet;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeQuery;

/**
 * A data storage that is synchronized over the entire cloud system
 */
public class SynchronizedPersistentDataStorage implements PersistentDataStorage {
    private static final Collection<Reference<? extends SynchronizedPersistentDataStorage>> storages = new CopyOnWriteArrayList<>();

    static {
        PacketAPI.instance().registerHandler(PacketNodeWrapperDataClearSet.class, packet -> {
            for (Reference<? extends SynchronizedPersistentDataStorage> ref : storages) {
                SynchronizedPersistentDataStorage storage = ref.get();
                if (storage == null) {
                    storages.remove(ref);
                    continue;
                }
                if (storage.key.equals(packet.key())) {
                    storage.lock.writeLock().lock();
                    storage.clearData();
                    storage.data.append(packet.data());
                    storage.lock.writeLock().unlock();
                    storage.notifyNotifiers();
                }
            }
            return null;
        });
        PacketAPI.instance().registerHandler(PacketNodeWrapperDataRemove.class, packet -> {
            for (Reference<? extends SynchronizedPersistentDataStorage> ref : storages) {
                SynchronizedPersistentDataStorage storage = ref.get();
                if (storage == null) {
                    storages.remove(ref);
                    continue;
                }
                if (storage.key.equals(packet.key())) {
                    try {
                        storage.lock.writeLock().lock();
                        if (storage.data.contains(packet.data().toString())) {
                            storage.data.remove(packet.data().toString());
                            storage.cache.remove(packet.data());
                        } else {
                            continue;
                        }
                    } finally {
                        storage.lock.writeLock().unlock();
                    }
                    storage.notifyNotifiers();
                }
            }
            return null;
        });
        PacketAPI.instance().registerHandler(PacketNodeWrapperDataSet.class, packet -> {
            for (Reference<? extends SynchronizedPersistentDataStorage> ref : storages) {
                SynchronizedPersistentDataStorage storage = ref.get();
                if (storage == null) {
                    storages.remove(ref);
                    continue;
                }
                if (storage.key.equals(packet.key())) {
                    storage.lock.writeLock().lock();
                    storage.data.append(packet.data());
                    for (String key : packet.data().keys()) {
                        storage.cache.remove(Key.fromString(key));
                    }
                    storage.lock.writeLock().unlock();
                }
            }
            return null;
        });
    }

    private final Key key;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private final Document.Mutable data = Document.newJsonDocument();

    public SynchronizedPersistentDataStorage(Key key) {
        this.key = key;
        this.data.append(new PacketWrapperNodeQuery(key).sendQuery(PacketData.class).data());
        storages.add(new WeakReference<>(this));
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
            for (String s : data.keys()) {
                keys.add(Key.fromString(s));
            }
        } finally {
            lock.readLock().unlock();
        }
        return Collections.unmodifiableCollection(keys);
    }

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        try {
            lock.writeLock().lock();
            data = type.clone(data);
            if (cache.containsKey(key) && cache.get(key).equals(data)) {
                return;
            }
            cache.put(key, data);
            Document.Mutable d = Document.newJsonDocument();
            type.serialize(d, key.toString(), data);
            this.data.append(d);
            new PacketWrapperNodeDataSet(this.key, d).sendAsync();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        T ret;
        try {
            lock.writeLock().lock();
            if (!data.contains(key.toString())) {
                return null;
            }
            T old = (T) cache.remove(key);
            if (old == null) {
                old = type.deserialize(data, key.toString());
            }
            data.remove(key.toString());
            new PacketWrapperNodeDataRemove(this.key, key).sendAsync();
            ret = type.clone(old);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return ret;
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
            if (!data.contains(key.toString())) {
                return null;
            }
            T value = type.clone(type.deserialize(data, key.toString()));
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
        T ret;
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
            if (data.contains(key.toString())) {
                T value = type.clone(type.deserialize(data, key.toString()));
                cache.put(key, value);
                return type.clone(value);
            }
            T val = type.clone(defaultValue.get());
            Document.Mutable d = Document.newJsonDocument();
            type.serialize(d, key.toString(), val);
            this.data.append(d);
            new PacketWrapperNodeDataSet(this.key, d).sendAsync();
            cache.put(key, val);
            ret = type.clone(val);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return ret;
    }

    @Override
    public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        try {
            lock.readLock().lock();
            if (this.data.contains(key.toString())) {
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (this.data.contains(key.toString())) {
                return;
            }
            data = type.clone(data);
            Document.Mutable d = Document.newJsonDocument();
            type.serialize(d, key.toString(), data);
            this.data.append(d);
            new PacketWrapperNodeDataSet(this.key, d).sendAsync();
            cache.put(key, data);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public boolean has(@NotNull Key key) {
        try {
            lock.readLock().lock();
            return data.contains(key.toString());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.writeLock().lock();
            clearData();
            new PacketWrapperNodeDataClearSet(key, Document.newJsonDocument()).sendAsync();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    private void clearData() {
        cache.clear();
        for (String s : new ArrayList<>(data.keys())) data.remove(s);
    }

    @Override
    public void loadFromJsonDocument(Document document) {
        try {
            lock.writeLock().lock();
            clearData();
            document = document.immutableCopy();
            new PacketWrapperNodeDataClearSet(key, document).sendAsync();
            data.append(document);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public Document storeToJsonDocument() {
        try {
            lock.readLock().lock();
            return data.immutableCopy();
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
        for (UpdateNotifier updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
