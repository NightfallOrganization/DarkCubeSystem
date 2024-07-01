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
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataClearSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataRemove;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeGetOrDefault;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeQuery;
import eu.darkcube.system.impl.cloudnet.node.userapi.UserNodePersistentDataStorage;
import eu.darkcube.system.impl.common.data.LegacyDataTransformer;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.PersistentDataStorage;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class SynchronizedPersistentDataStorages {
    private static final DatabaseProvider databaseProvider;
    private static final Gson gson = new Gson();
    private static final ReferenceQueue<SynchronizedPersistentDataStorage> queue = new ReferenceQueue<>();
    private static final Lock lock = new ReentrantLock(false);
    private static final Map<String, Map<Key, StorageReference>> storages = new HashMap<>();
    private static final Map<String, Map<Key, PersistentDataStorage>> wrappers = new HashMap<>();
    private static final Map<String, Function<PersistentDataStorage, StorageImplementation<?>>> STORAGE_IMPLEMENTATIONS = new HashMap<>();

    static {
        databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataSet.class, new HandlerSet());
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataClearSet.class, new HandlerClearSet());
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataRemove.class, new HandlerRemove());
        PacketAPI.instance().registerHandler(PacketWrapperNodeQuery.class, new HandlerQuery());
        PacketAPI.instance().registerHandler(PacketWrapperNodeGetOrDefault.class, new HandlerGetOrDefault());
        new CollectorHandler().start();
        STORAGE_IMPLEMENTATIONS.put("userapi_users", storage -> new UserNodePersistentDataStorage.Implementation());
    }

    public static String toString(Key key) {
        if (key.namespace().isEmpty()) return key.value();
        return key.asString();
    }

    public static SynchronizedPersistentDataStorage storage(String table, Key key) {
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
                storages.computeIfAbsent(table, s -> new HashMap<>()).put(key, ref);
                wrappers.computeIfAbsent(table, s -> new HashMap<>()).put(key, wrapped);
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
        var storage = new SynchronizedPersistentDataStorage(database, key);
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

    private static <T extends PersistentDataStorage> Tuple2<T, Document> setupStorage(SynchronizedPersistentDataStorage storage, String table, Document document) {
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
            storageImplementation = data -> new StorageImplementation.Default();
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

    private static class CollectorHandler extends Thread {
        public CollectorHandler() {
            setName("SynchronizedStorageGCHandler");
            setDaemon(true);
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
