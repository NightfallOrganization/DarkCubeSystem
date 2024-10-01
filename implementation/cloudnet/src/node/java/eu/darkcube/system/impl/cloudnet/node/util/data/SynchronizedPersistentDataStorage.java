/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.key.KeyPattern;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data storage that is synchronized over the entire cloud system
 */
@SuppressWarnings("PatternValidation")
public final class SynchronizedPersistentDataStorage implements PersistentDataStorage {
    static final String CHANNEL = "darkcube-persistent-data";
    private static final Logger LOGGER = LoggerFactory.getLogger("SynchronizedPersistentDataStorage");
    private final String table;
    private final Key key;
    private final Database database;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private final JsonObject data = new JsonObject();
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private final AtomicBoolean saving = new AtomicBoolean(false);
    private final AtomicBoolean saveAgain = new AtomicBoolean(false);
    private int state;
    public Function<Document, Document> documentSaver = d -> d;

    SynchronizedPersistentDataStorage(Database database, String table, Key key) {
        this.database = database;
        this.table = table;
        this.key = key;
    }

    int set(Key key, JsonElement data) {
        return this.sendUpdate(() -> {
            this.data.add(key.toString(), data.deepCopy());
            this.cache.remove(key);
            return UpdateResult.CHANGED;
        }).state();
    }

    int removePlain(Key key) {
        return sendUpdate(() -> {
            if (!data.has(key.toString())) return UpdateResult.NOTHING;
            data.remove(key.toString());
            cache.remove(key);
            return UpdateResult.CHANGED;
        }).state();
    }

    @NotNull
    RemoveComplex removeComplex(Key key) {
        var update = sendUpdate(() -> {
            if (!data.has(key.toString())) return UpdateResult.NOTHING;
            var json = data.remove(key.toString());
            cache.remove(key);
            return new UpdateResult(true, json);
        });
        var json = (JsonElement) update.other();
        return new RemoveComplex(update.state(), json);
    }

    @NotNull
    GetOrDefault getOrDefault(Key key, JsonElement defaultJson) {
        var update = sendUpdate(() -> {
            if (this.data.has(key.toString())) {
                var data = this.data.get(key.toString());
                return new UpdateResult(false, data);
            }
            data.add(key.toString(), defaultJson.deepCopy());
            return new UpdateResult(true, defaultJson);
        });
        var state = update.state();
        var json = (JsonElement) Objects.requireNonNull(update.other());
        return new GetOrDefault(state, json);
    }

    int clear0() {
        return sendUpdate(() -> {
            this.data.asMap().clear();
            this.cache.clear();
            return UpdateResult.CHANGED;
        }).state();
    }

    int loadFromJson(JsonObject json) {
        return sendUpdate(() -> {
            this.data.asMap().clear();
            this.cache.clear();
            this.data.asMap().putAll(json.deepCopy().asMap());
            return UpdateResult.CHANGED;
        }).state();
    }

    Query query() {
        try {
            lock.readLock().lock();
            return new Query(state, data.deepCopy());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public @NotNull Collection<Key> keys() {
        List<Key> keys = new ArrayList<>();
        try {
            lock.readLock().lock();
            for (@KeyPattern var s : data.keySet()) {
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
        set(key, type.serialize(data));
    }

    @Override
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        var json = removeComplex(key).json();
        if (json == null) return null;
        return type.deserialize(json);
    }

    @Override
    public void remove(@NotNull Key key) {
        removePlain(key);
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
            var value = type.deserialize(data.get(key.toString()));
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
                return type.clone((T) cache.get(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        var json = type.serialize(defaultValue.get());
        var response = getOrDefault(key, json);
        return type.deserialize(response.json());
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
        var json = type.serialize(data);
        getOrDefault(key, json);
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
        clear0();
    }

    @Override
    public void loadFromJsonObject(@NotNull JsonObject object) {
        loadFromJson(object);
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

    private Updated sendUpdate(Updater task) {
        int newState;
        JsonObject dataCopy;
        @Nullable Object other;
        try {
            lock.writeLock().lock();
            var result = task.update();
            if (!result.changed()) {
                return new Updated(state, result.other()); // Return current state, nothing changed
            }
            newState = ++state;
            dataCopy = data.deepCopy();
            other = result.other();
        } finally {
            lock.writeLock().unlock();
        }
        LOGGER.debug("Send update table {} key {} state {}", table, key, newState);
        ChannelMessage.builder().channel(CHANNEL).targetServices().message("update-data").buffer(DataBuf.empty().writeString(this.table).writeObject(this.key).writeInt(newState).writeObject(dataCopy)).build().send();
        notifyNotifiers();
        return new Updated(newState, other);
    }

    record GetOrDefault(int state, @NotNull JsonElement json) {
    }

    record RemoveComplex(int state, @Nullable JsonElement json) {
    }

    record Query(int state, @NotNull JsonObject data) {
    }

    private record Updated(int state, @Nullable Object other) {
    }

    private interface Updater {
        UpdateResult update();
    }

    private record UpdateResult(boolean changed, @Nullable Object other) {
        private static final UpdateResult CHANGED = new UpdateResult(true, null);
        private static final UpdateResult NOTHING = new UpdateResult(false, null);
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
            var document = DocumentFactory.json().parse(storeToJsonObject().toString());
            var docToSave = documentSaver.apply(document);
            var fut = database.insertAsync(SynchronizedPersistentDataStorages.toString(key), docToSave);
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

    private void notifyNotifiers() {
        save(); // Do this here cuz were lazy
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }

    public Key key() {
        return key;
    }
}
