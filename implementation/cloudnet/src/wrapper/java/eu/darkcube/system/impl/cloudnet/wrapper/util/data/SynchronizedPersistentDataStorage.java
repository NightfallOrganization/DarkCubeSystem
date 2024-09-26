/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.util.data;

import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
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
final class SynchronizedPersistentDataStorage implements PersistentDataStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedPersistentDataStorage.class);

    static final String CHANNEL = "darkcube-persistent-data";

    private final String table;
    private final Key key;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private final JsonObject data = new JsonObject();
    private volatile int state = -1;
    private final Condition newStateAvailable = this.lock.writeLock().newCondition();

    public SynchronizedPersistentDataStorage(String table, Key key) {
        this.table = table;
        this.key = key;

        loadData();
    }

    Key key() {
        return key;
    }

    String table() {
        return table;
    }

    void updateData(int state, @NotNull JsonObject newData) {
        this.lock.writeLock().lock();
        try {
            if (this.state - state > 0) return; // Data is out of date, we already have newer data available
            this.data.asMap().clear();
            this.data.asMap().putAll(newData.asMap());
            this.cache.clear();
            this.state = state;
            LOGGER.info("Table {} key {} update state: {}", table, key, state);
            this.newStateAvailable.signalAll();
        } finally {
            this.lock.writeLock().unlock();
        }
        this.notifyNotifiers();
    }

    private void loadData() {
        var startTime = currentTimeMillis();
        try {
            var response = sendQuery("query", DataBuf.empty().writeString(this.table).writeObject(this.key));
            this.state = response.content().readInt();
            LOGGER.info("Table {} key {} initial state: {}", table, key, state);
            this.data.asMap().putAll(response.content().readObject(JsonObject.class).asMap());
        } finally {
            LOGGER.info("Time for loadData({}, {}): {}", this.table, this.key, currentTimeMillis() - startTime);
        }
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
        var startTime = currentTimeMillis();
        try {
            var json = type.serialize(data);
            awaitState("set", DataBuf.empty().writeString(this.table).writeObject(this.key).writeObject(key).writeObject(json));
        } finally {
            LOGGER.info("Time for set({}, {}, {}): {}ms", this.table, this.key, key, currentTimeMillis() - startTime);
        }
    }

    @Override
    public void remove(@NotNull Key key) {
        var startTime = currentTimeMillis();
        try {
            awaitState("remove-plain", DataBuf.empty().writeString(this.table).writeObject(this.key).writeObject(key));
        } finally {
            LOGGER.info("Time for remove({}, {}, {}): {}ms", this.table, this.key, key, currentTimeMillis() - startTime);
        }
    }

    @Override
    @Nullable
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        var startTime = currentTimeMillis();
        try {
            var response = sendQuery("remove-with-response", DataBuf.empty().writeString(this.table).writeObject(this.key).writeObject(key));
            awaitState(response);
            var removed = response.content().readBoolean();
            if (!removed) return null;
            var jsonResponse = response.content().readObject(JsonElement.class);
            return type.deserialize(jsonResponse);
        } finally {
            LOGGER.info("Time for removeComplex({}, {}, {}): {}ms", this.table, this.key, key, currentTimeMillis() - startTime);
        }
    }

    @Override
    public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            this.lock.readLock().lock();
            if (this.cache.containsKey(key)) {
                return type.clone((T) this.cache.get(key));
            }
        } finally {
            this.lock.readLock().unlock();
        }
        try {
            this.lock.writeLock().lock();
            if (this.cache.containsKey(key)) {
                return type.clone((T) this.cache.get(key));
            }
            if (!this.data.has(key.toString())) {
                return null;
            }
            var value = type.clone(type.deserialize(this.data.get(key.toString())));
            this.cache.put(key, value);
            return type.clone(value);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public @NotNull <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
        var startTime = currentTimeMillis();
        var network = false;
        try {
            var val = this.get(key, type);
            if (val != null) return val;

            network = true;
            var json = type.serialize(defaultValue.get());
            var response = this.sendQuery("get-or-default", DataBuf.empty().writeString(this.table).writeObject(this.key).writeObject(key).writeObject(json));
            this.awaitState(response);
            // Return value is sent in response - someone else could have removed the value
            var jsonResponse = response.content().readObject(JsonElement.class);
            return type.deserialize(jsonResponse);
        } finally {
            if (network) {
                LOGGER.info("Time for getOrDefault({}, {}, {}): {}ms", table, this.key, key, currentTimeMillis() - startTime);
            }
        }
    }

    @Override
    public <T> void setIfAbsent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        this.get(key, type, () -> data);
    }

    @Override
    public boolean has(@NotNull Key key) {
        try {
            this.lock.readLock().lock();
            return this.data.has(key.toString());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        awaitState("clear", DataBuf.empty().writeString(this.table).writeObject(this.key));
    }

    @Override
    public void loadFromJsonObject(@NotNull JsonObject json) {
        awaitState("load-from-json", DataBuf.empty().writeString(this.table).writeObject(this.key).writeObject(json));
    }

    private @NotNull ChannelMessage sendQuery(@NotNull String message, @NotNull DataBuf dataBuf) {
        return Objects.requireNonNull(ChannelMessage.builder().channel(CHANNEL).targetNodes().message(message).buffer(dataBuf).build().sendSingleQuery());
    }

    private void awaitState(@NotNull String message, @NotNull DataBuf dataBuf) {
        var query = sendQuery(message, dataBuf);
        awaitState(query);
    }

    private void awaitState(@NotNull ChannelMessage queryResponse) {
        var state = queryResponse.content().readInt();
        awaitState(state);
    }

    private void awaitState(int state) {
        if (this.state - state >= 0) return; // State already up-to-date
        LOGGER.info("Table {} key {} wait for state: {}", table, key, state);
        this.lock.writeLock().lock();
        try {
            // Wait for the state to be at least the given state, or newer
            while (true) {
                if (this.state - state >= 0) return; // State already up-to-date
                this.newStateAvailable.awaitUninterruptibly();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public @NotNull JsonObject storeToJsonObject() {
        try {
            this.lock.readLock().lock();
            return data.deepCopy();
        } finally {
            this.lock.readLock().unlock();
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

    public void notifyNotifiers() {
        for (var updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
