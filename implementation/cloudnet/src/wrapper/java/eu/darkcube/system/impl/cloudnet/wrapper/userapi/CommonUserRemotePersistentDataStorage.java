/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.userapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataGetOrDefault;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataLoad;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataRemove;
import eu.darkcube.system.cloudnet.userapi.packets.PacketWNUserPersistentDataSet;
import eu.darkcube.system.impl.cloudnet.userapi.CommonPersistentDataStorage;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;

public class CommonUserRemotePersistentDataStorage implements CommonPersistentDataStorage {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final UUID uniqueId;
    private final Map<Key, Object> caches = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private final JsonObject data = new JsonObject();

    public CommonUserRemotePersistentDataStorage(UUID uniqueId, JsonObject initialData) {
        this.uniqueId = uniqueId;
        this.data.asMap().putAll(initialData.asMap());
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
                keys.add(Key.key(s));
            }
        } finally {
            lock.readLock().unlock();
        }
        return Collections.unmodifiableCollection(keys);
    }

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        var json = type.serialize(data);
        new PacketWNUserPersistentDataSet(uniqueId, key, json).sendQuery(PacketWNUserPersistentDataSet.Result.class);
    }

    @Override
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        var result = new PacketWNUserPersistentDataRemove(uniqueId, key).sendQuery(PacketWNUserPersistentDataRemove.Result.class);
        var removed = result.removed();
        if (removed == null) {
            return null;
        }
        return type.deserialize(removed);
    }

    @Override
    public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            lock.readLock().lock();
            if (caches.containsKey(key)) {
                return type.clone((T) caches.get(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (caches.containsKey(key)) {
                return type.clone((T) caches.get(key));
            }
            if (!data.has(key.toString())) {
                return null;
            }
            var value = type.clone(type.deserialize(data.get(key.toString())));
            caches.put(key, value);
            return type.clone(value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
        try {
            lock.readLock().lock();
            if (caches.containsKey(key)) {
                return type.clone((T) caches.get(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        var data = defaultValue.get();
        var json = type.serialize(data);
        var result = new PacketWNUserPersistentDataGetOrDefault(this.uniqueId, key, json).sendQuery(PacketWNUserPersistentDataGetOrDefault.Result.class);
        var parsed = type.deserialize(result.data());
        try {
            lock.writeLock().lock();
            var old = (T) caches.putIfAbsent(key, parsed);
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

    @Override
    public void loadFromJsonObject(@NotNull JsonObject json) {
        new PacketWNUserPersistentDataLoad(uniqueId, json.deepCopy()).sendQuery(PacketWNUserPersistentDataLoad.Result.class);
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
    public void clearCache() {
        try {
            lock.writeLock().lock();
            caches.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return Collections.unmodifiableCollection(updateNotifiers);
    }

    @Override
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.add(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.remove(notifier);
    }

    @Override
    public void remove(@NotNull Key key) {
        var changed = false;
        try {
            lock.writeLock().lock();
            if (data.has(key.toString())) {
                changed = true;
                data.remove(key.toString());
                caches.remove(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (changed) notifyNotifiers();
    }

    @Override
    public JsonElement getOrDefault(Key key, JsonElement json) {
        try {
            lock.readLock().lock();
            if (data.has(key.toString())) {
                return data.get(key.toString());
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (data.has(key.toString())) {
                return data.get(key.toString());
            }
            data.add(key.toString(), json);
            return json;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void set(Key key, JsonElement json) {
        try {
            lock.writeLock().lock();
            data.add(key.toString(), json);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void merge(@NotNull JsonObject data) {
        try {
            lock.writeLock().lock();
            this.data.asMap().putAll(data.asMap());
            this.caches.clear();
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(@NotNull JsonObject data) {
        try {
            lock.writeLock().lock();
            this.data.asMap().clear();
            this.caches.clear();
            this.data.asMap().putAll(data.asMap());
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void notifyNotifiers() {
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
