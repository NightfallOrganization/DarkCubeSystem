/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi;

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
import java.util.logging.Logger;

import eu.darkcube.system.cloudnet.userapi.packets.PacketNWUserPersistentDataMerge;
import eu.darkcube.system.cloudnet.userapi.packets.PacketNWUserPersistentDataRemove;
import eu.darkcube.system.cloudnet.userapi.packets.PacketNWUserPersistentDataUpdate;
import eu.darkcube.system.impl.cloudnet.userapi.CommonPersistentDataStorage;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;

public class UserLocalPersistentDataStorage implements CommonPersistentDataStorage {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final UUID uniqueId;
    private volatile String name;
    private final JsonObject data = new JsonObject();
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();

    public UserLocalPersistentDataStorage(UUID uniqueId, String name, JsonObject initialData) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.data.asMap().putAll(initialData.asMap());
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    @Override
    public @NotNull @UnmodifiableView PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Override
    public @NotNull @Unmodifiable Collection<Key> keys() {
        List<Key> keys = new ArrayList<>();
        try {
            lock.readLock().lock();
            for (var key : data.keySet()) {
                keys.add(Key.key(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        return Collections.unmodifiableList(keys);
    }

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        var json = type.serialize(data);
        set(key, json);
    }

    @Override
    public <T> @Nullable T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        T ret;
        try {
            lock.writeLock().lock();
            var contains = data.has(key.toString());
            if (!contains) return null;
            var t = (T) cache.remove(key);
            var json = data.get(key.toString());
            if (t == null) {
                t = type.deserialize(json);
            }
            ret = t;
        } finally {
            lock.writeLock().unlock();
        }
        new PacketNWUserPersistentDataRemove(uniqueId, key).sendSync();
        notifyNotifiers();
        return ret;
    }

    @Override
    public <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            lock.readLock().lock();
            if (cache.containsKey(key)) {
                try {
                    return type.clone((T) cache.get(key));
                } catch (ClassCastException ex) {
                    // TODO Corrupt cache
                    Logger.getGlobal().severe("Cache corruption in UserPersistentData: " + this.name + "(" + this.uniqueId + ")");
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key)) {
                try {
                    return type.clone((T) cache.get(key));
                } catch (ClassCastException ex) {
                    // TODO Corrupt cache
                    Logger.getGlobal().severe("Cache corruption in UserPersistentData: " + this.name + "(" + this.uniqueId + ")");
                }
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
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        try {
            lock.readLock().lock();
            if (cache.containsKey(key)) {
                try {
                    return type.clone((T) cache.get(key));
                } catch (ClassCastException ex) {
                    // TODO corrupt cache
                    Logger.getGlobal().severe("Cache corruption in UserPersistentData: " + this.name + "(" + this.uniqueId + ")");
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key)) {
                try {
                    return type.clone((T) cache.get(key));
                } catch (ClassCastException ex) {
                    // TODO corrupt cache
                    Logger.getGlobal().severe("Cache corruption in UserPersistentData: " + this.name + "(" + this.uniqueId + ")");
                }
            }
            if (data.has(key.toString())) {
                var value = type.clone(type.deserialize(data.get(key.toString())));
                cache.put(key, value);
                return type.clone(value);
            }
            var val = type.clone(defaultValue.get());
            var json = type.serialize(val);
            data.add(key.toString(), json);
            cache.put(key, val);

            notifyNotifiers();
            return val;
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
        try {
            lock.writeLock().lock();
            data.asMap().clear();
            cache.clear();
            data.asMap().putAll(json.asMap());
        } finally {
            lock.writeLock().unlock();
        }
        new PacketNWUserPersistentDataUpdate(uniqueId, json).sendAsync();
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
        return updateNotifiers;
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
        this.updateNotifiers.add(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        this.updateNotifiers.remove(notifier);
    }

    @Override
    public void remove(@NotNull Key key) {
        var changed = false;
        try {
            lock.writeLock().lock();
            if (data.has(key.toString())) {
                changed = true;
                data.remove(key.toString());
                cache.remove(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (changed) notifyNotifiers();
    }

    @Override
    public JsonElement getOrDefault(Key key, JsonElement json) {
        return null;
    }

    @Override
    public void set(Key key, JsonElement json) {
        var d = new JsonObject();
        d.add(key.toString(), json);
        try {
            lock.writeLock().lock();
            this.data.add(key.toString(), json);
            cache.remove(key);
            new PacketNWUserPersistentDataMerge(uniqueId, d).sendSync();
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void merge(JsonObject json) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(JsonObject json) {
        throw new UnsupportedOperationException();
    }

    private void notifyNotifiers() {
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
