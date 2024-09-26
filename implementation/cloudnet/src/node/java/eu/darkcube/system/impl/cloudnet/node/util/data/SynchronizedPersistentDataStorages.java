/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.darkcube.system.impl.cloudnet.node.userapi.UserNodePersistentDataStorage;
import eu.darkcube.system.impl.common.data.LegacyDataTransformer;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.PersistentDataStorage;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedPersistentDataStorages {
    private static final DatabaseProvider databaseProvider;
    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedPersistentDataStorages.class);
    private static final Gson gson = new Gson();
    private static final ReferenceQueue<SynchronizedPersistentDataStorage> queue = new ReferenceQueue<>();
    private static final Lock lock = new ReentrantLock(false);
    private static final Map<String, Map<Key, StorageReference>> storages = new HashMap<>();
    private static final Map<String, Map<Key, PersistentDataStorage>> wrappers = new HashMap<>();
    private static final Map<String, Function<PersistentDataStorage, StorageImplementation<?>>> STORAGE_IMPLEMENTATIONS = new HashMap<>();

    static {
        databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        new CollectorHandler().start();
        STORAGE_IMPLEMENTATIONS.put("userapi_users", _ -> new UserNodePersistentDataStorage.Implementation());

        InjectionLayer.boot().instance(EventManager.class).registerListener(new Listener());
    }

    public static class Listener {
        @EventListener
        public void handle(ChannelMessageReceiveEvent event) {
            if (!event.channel().equals(SynchronizedPersistentDataStorage.CHANNEL)) return;
            var content = event.content();
            switch (event.message()) {
                case "query" -> query(event, content);
                case "set" -> set(event, content);
                case "remove-plain" -> removePlain(event, content);
                case "remove-with-response" -> removeWithResponse(event, content);
                case "get-or-default" -> getOrDefault(event, content);
                case "clear" -> clear(event, content);
                case "load-from-json" -> loadFromJson(event, content);
                default -> LOGGER.error("Unknown message {}", event.message());
            }
        }

        private void query(ChannelMessageReceiveEvent event, DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);

            var storage = storage(table, key);
            var entry = storage.query();
            event.binaryResponse(DataBuf.empty().writeInt(entry.state()).writeObject(entry.data()));
        }

        private void set(ChannelMessageReceiveEvent event, DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);
            var entryKey = buf.readObject(Key.class);
            var dataJson = buf.readObject(JsonElement.class);

            var storage = storage(table, key);
            var state = storage.set(entryKey, dataJson);
            event.binaryResponse(DataBuf.empty().writeInt(state));
        }

        private void removePlain(ChannelMessageReceiveEvent event, DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);
            var entryKey = buf.readObject(Key.class);

            var storage = storage(table, key);
            var state = storage.removePlain(entryKey);
            event.binaryResponse(DataBuf.empty().writeInt(state));
        }

        private void removeWithResponse(ChannelMessageReceiveEvent event, DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);
            var entryKey = buf.readObject(Key.class);

            var storage = storage(table, key);
            var response = storage.removeComplex(entryKey);
            var state = response.state();
            var json = response.json();
            var r = DataBuf.empty().writeInt(state);
            r.writeBoolean(json != null);
            if (json != null) r.writeObject(json);
            event.binaryResponse(r);
        }

        private void getOrDefault(ChannelMessageReceiveEvent event, DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);
            var entryKey = buf.readObject(Key.class);
            var defaultValueJson = buf.readObject(JsonElement.class);

            var storage = storage(table, key);
            var response = storage.getOrDefault(entryKey, defaultValueJson);
            var state = response.state();
            var json = response.json();
            event.binaryResponse(DataBuf.empty().writeInt(state).writeObject(json));
        }

        private void clear(ChannelMessageReceiveEvent event, DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);

            var storage = storage(table, key);
            var state = storage.clear0();
            event.binaryResponse(DataBuf.empty().writeInt(state));
        }

        private void loadFromJson(ChannelMessageReceiveEvent event, DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);
            var json = buf.readObject(JsonObject.class);

            var storage = storage(table, key);
            var state = storage.loadFromJson(json);
            event.binaryResponse(DataBuf.empty().writeInt(state));
        }
    }

    public static String toString(Key key) {
        if (key.namespace().isEmpty()) return key.value();
        return key.asString();
    }

    private static SynchronizedPersistentDataStorage storage(String table, Key key) {
        return storageTuple(table, key)._1();
    }

    public static Tuple2<SynchronizedPersistentDataStorage, PersistentDataStorage> storageTuple(String table, Key key) {
        try {
            lock.lock();
            @Nullable var map = storages.get(table);
            @Nullable var ref = map == null ? null : map.getOrDefault(key, null);
            var storage = ref == null ? null : ref.get();
            if (storage == null) {
                var tuple = load(table, key);

                storage = tuple._1();
                var wrapped = tuple._2();
                ref = new StorageReference(storage, queue, table, key);
                storages.computeIfAbsent(table, _ -> new HashMap<>()).put(key, ref);
                wrappers.computeIfAbsent(table, _ -> new HashMap<>()).put(key, wrapped);
                return tuple;
            }
            var wrapper = wrappers.get(table).get(key);
            return Tuple.of(storage, wrapper);
        } finally {
            lock.unlock();
        }
    }

    private static Tuple2<SynchronizedPersistentDataStorage, PersistentDataStorage> load(String table, Key key) {
        var database = databaseProvider.database(table);
        var toString = toString(key);
        var document = loadDocument(database, toString);
        var storage = new SynchronizedPersistentDataStorage(database, table, key);
        var tuple = setupStorage(storage, table, document);
        var persistentData = tuple._2();
        if (persistentData != null) {
            var object = gson.fromJson(persistentData.serializeToString(), JsonObject.class);
            LegacyDataTransformer.transformLegacyPersistentData(object);
            storage.loadFromJsonObject(object);
        }
        var wrapper = tuple._1();
        return Tuple.of(storage, wrapper);
    }

    private static <T extends PersistentDataStorage> Tuple2<T, @Nullable Document> setupStorage(SynchronizedPersistentDataStorage storage, String table, @Nullable Document document) {
        var implementation = (StorageImplementation<T>) findImplementation(table, storage);
        var wrapped = implementation.wrapper(storage, document);
        storage.documentSaver = implementation.saver(wrapped._1());
        return wrapped;
    }

    private static @Nullable Document loadDocument(Database database, String toString) {
        return database.get(toString);
    }

    private static StorageImplementation<?> findImplementation(String table, SynchronizedPersistentDataStorage storage) {
        var storageImplementation = STORAGE_IMPLEMENTATIONS.get(table);
        if (storageImplementation == null) {
            storageImplementation = _ -> new StorageImplementation.Default();
        }
        return storageImplementation.apply(storage);
    }

    public static void init() {
    }

    public static void exit() {
    }

    private static class StorageReference extends SoftReference<SynchronizedPersistentDataStorage> {
        private final String table;
        private final Key key;

        public StorageReference(SynchronizedPersistentDataStorage referent, ReferenceQueue<? super SynchronizedPersistentDataStorage> q, String table, Key key) {
            super(referent, q);
            this.table = table;
            this.key = key;
        }
    }

    private static class CollectorHandler implements Runnable {
        public void start() {
            Thread.ofVirtual().name("SynchronizedStorageGCHandler").start(this);
        }

        @Override
        public void run() {
            // noinspection InfiniteLoopStatement
            while (true) {
                try {
                    var reference = (StorageReference) queue.remove();
                    lock.lock();
                    var map = storages.get(reference.table);
                    map.remove(reference.key);

                    wrappers.get(reference.table).remove(reference.key);
                    if (map.isEmpty()) {
                        storages.remove(reference.table);
                        wrappers.remove(reference.table);
                    }
                    lock.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
