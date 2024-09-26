/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.standalone.util.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.darkcube.system.impl.standalone.util.data.database.SQLDatabase;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandalonePersistentDataStorage implements PersistentDataStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("StandalonePersistentDataStorage");
    private final SQLDatabase database;
    private final String table;
    private final Key key;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private final JsonObject data = new JsonObject();
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private final AtomicBoolean saving = new AtomicBoolean(false);
    private final AtomicBoolean saveAgain = new AtomicBoolean(false);
    public Function<JsonObject, JsonObject> documentSaver = d -> d;

    public StandalonePersistentDataStorage(SQLDatabase database, String table, Key key) {
        this.database = database;
        this.table = table;
        this.key = key;
    }

    public Key key() {
        return key;
    }

    @Override
    public @NotNull @UnmodifiableView PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Override
    public @NotNull @Unmodifiable Collection<@NotNull Key> keys() {
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
        try {
            lock.writeLock().lock();
            data = type.clone(data);
            if (cache.containsKey(key) && cache.get(key).equals(data)) {
                return;
            }
            cache.put(key, data);
            var json = type.serialize(data);
            this.data.add(key.toString(), json);
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T> @Nullable T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return remove0(key, type);
    }

    public <T> T remove0(@NotNull Key key, @Nullable PersistentDataType<T> type) {
        T ret;
        try {
            lock.writeLock().lock();
            if (!data.has(key.toString())) {
                return null;
            }
            var old = (T) cache.remove(key);
            if (old == null && type != null) {
                old = type.deserialize(data.get(key.toString()));
            }
            data.remove(key.toString());
            ret = type != null ? type.clone(old) : null;
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
        return ret;
    }

    @Override
    public void remove(@NotNull Key key) {
        remove0(key);
    }

    public @Nullable JsonElement remove0(@NotNull Key key) {
        try {
            lock.writeLock().lock();
            if (!data.has(key.toString())) {
                return null;
            }
            cache.remove(key);
            var removed = data.remove(key.toString());
            notifyNotifiers();
            return removed;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
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

    public JsonElement get(Key key, Supplier<JsonElement> defaultValue) {
        try {
            lock.readLock().lock();
            if (data.has(key.toString())) {
                return data.get(key.toString()).deepCopy();
            }
        } finally {
            lock.readLock().unlock();
        }
        var value = defaultValue.get().deepCopy();
        try {
            lock.writeLock().lock();
            if (data.has(key.toString())) {
                return data.get(key.toString()).deepCopy();
            }
            data.add(key.toString(), value);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return value.deepCopy();
    }

    @Override
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
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
            if (data.has(key.toString())) {
                var value = type.clone(type.deserialize(data.get(key.toString())));
                cache.put(key, value);
                return type.clone(value);
            }
            var val = type.clone(defaultValue.get());
            var json = type.serialize(val);
            this.data.add(key.toString(), json);
            cache.put(key, val);
            ret = type.clone(val);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return ret;
    }

    @Override
    public <T> void setIfAbsent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
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
            clearData();
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void loadFromJsonObject(@NotNull JsonObject object) {
        try {
            lock.writeLock().lock();
            clearData();
            data.asMap().putAll(object.asMap());
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
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

    public void set(Key key, JsonElement data) {
        try {
            lock.writeLock().lock();
            this.data.add(key.toString(), data.deepCopy());
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
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

    private void save() {
        if (saving.compareAndSet(false, true)) {
            var document = storeToJsonObject();
            var docToSave = documentSaver.apply(document);
            var fut = database.setAsync(table, StandaloneCustomPersistentDataProvider.toString(key), docToSave);
            fut.thenAccept(success -> {
                if (success) {
                    saving.set(false);
                    if (saveAgain.compareAndSet(true, false)) {
                        save();
                    }
                } else {
                    LOGGER.error("Failed to save - trying again");
                    save();
                }
            });
            fut.exceptionally(t -> {
                saving.set(false);
                t.printStackTrace();
                return null;
            });
        } else {
            saveAgain.set(true);
            if (!saving.get()) {
                save();
            }
        }
    }

    private void clearData() {
        cache.clear();
        data.asMap().clear();
    }

    private void notifyNotifiers() {
        save(); // Do this here cuz were lazy
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
