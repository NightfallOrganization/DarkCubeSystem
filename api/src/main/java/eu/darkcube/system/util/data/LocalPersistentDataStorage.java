/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

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

import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

public class LocalPersistentDataStorage implements PersistentDataStorage {
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final Map<Key, Object> cache = new HashMap<>();
    protected final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    protected final JsonObject data = new JsonObject();

    public void appendDocument(JsonObject document) {
        try {
            lock.writeLock().lock();
            for (var key : document.keySet()) {
                cache.remove(Key.key(key));
            }
            data.asMap().putAll(document.asMap());
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
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
        data = type.clone(data);
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key) && cache.get(key).equals(data)) {
                return;
            }
            cache.put(key, data);
            var json = type.serialize(data);
            this.data.add(key.toString(), json);
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
            if (!data.has(key.toString())) {
                return null;
            }
            var old = (T) cache.remove(key);
            if (old == null) {
                old = type.deserialize(data.get(key.toString()));
            }
            data.remove(key.toString());
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
                try {
                    return type.clone((T) cache.get(key));
                } catch (ClassCastException ex) {
                    // TODO Corrupt cache. This happens when the PServer uses its unsafe modify strategy
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
                    // TODO Corrupt cache. This happens when the PServer uses its unsafe modify strategy
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
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
        try {
            lock.readLock().lock();
            if (cache.containsKey(key)) {
                try {
                    return type.clone((T) cache.get(key));
                } catch (ClassCastException ex) {
                    // TODO Corrupt cache. This happens when the PServer uses its unsafe modify strategy
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        T ret;
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key)) {
                try {
                    return type.clone((T) cache.get(key));
                } catch (ClassCastException ex) {
                    // TODO Corrupt cache. This happens when the PServer uses its unsafe modify strategy
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
            ret = val;
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
            if (this.data.has(key.toString())) {
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (this.data.has(key.toString())) {
                return;
            }
            data = type.clone(data);
            var json = type.serialize(data);
            this.data.add(key.toString(), json);
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
            return data.has(key.toString());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.writeLock().lock();
            data.asMap().clear();
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public void loadFromJsonObject(@NotNull JsonObject object) {
        try {
            lock.writeLock().lock();
            data.asMap().clear();
            cache.clear();
            data.asMap().putAll(object.asMap());
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
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
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.add(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.remove(notifier);
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

    protected void notifyNotifiers() {
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
