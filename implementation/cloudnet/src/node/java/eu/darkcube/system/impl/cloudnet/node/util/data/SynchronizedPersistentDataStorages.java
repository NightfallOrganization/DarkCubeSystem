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

import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataClearSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataRemove;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeDataSet;
import eu.darkcube.system.cloudnet.util.data.packets.PacketWrapperNodeQuery;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public class SynchronizedPersistentDataStorages {
    private static final DatabaseProvider databaseProvider;
    private static final Gson gson = new Gson();
    private static final ReferenceQueue<SynchronizedPersistentDataStorage> queue = new ReferenceQueue<>();
    private static final Lock lock = new ReentrantLock(false);
    private static final Map<String, Map<Key, StorageReference>> storages = new HashMap<>();

    static {
        databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataSet.class, new HandlerSet());
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataClearSet.class, new HandlerClearSet());
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataRemove.class, new HandlerRemove());
        PacketAPI.instance().registerHandler(PacketWrapperNodeQuery.class, new HandlerQuery());
        new CollectorHandler().start();
    }

    public static String toString(Key key) {
        if (key.namespace().isEmpty()) return key.value();
        return key.asString();
    }

    public static SynchronizedPersistentDataStorage storage(String table, Key key) {
        lock.lock();
        var map = storages.get(table);
        var ref = map.getOrDefault(key, null);
        var storage = ref == null ? null : ref.get();
        if (storage == null) {
            var database = databaseProvider.database(table);
            var toString = toString(key);
            storage = new SynchronizedPersistentDataStorage(database, key);
            var doc = database.get(key.value());
            if (doc != null) {
                database.delete(key.value());
                database.insert(toString, doc);
                var object = gson.fromJson(doc.serializeToString(), JsonObject.class);
                storage.loadFromJsonObject(object);
            } else {
                doc = database.get(toString);
                if (doc != null) {
                    var object = gson.fromJson(doc.serializeToString(), JsonObject.class);
                    storage.loadFromJsonObject(object);
                }
            }
            ref = new StorageReference(storage, queue, table, key);
            storages.computeIfAbsent(table, s -> new HashMap<>()).put(key, ref);
        }
        lock.unlock();
        return storage;
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
                    if (map.isEmpty()) {
                        storages.remove(reference.table);
                    }
                    lock.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
