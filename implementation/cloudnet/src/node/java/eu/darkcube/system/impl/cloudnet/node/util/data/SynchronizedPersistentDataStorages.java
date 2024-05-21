/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.HashBiMap;
import eu.cloudnetservice.driver.database.Database;
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
    static final Database database;
    private static final Gson gson = new Gson();
    private static final ReferenceQueue<SynchronizedPersistentDataStorage> queue = new ReferenceQueue<>();
    private static final Lock lock = new ReentrantLock(false);
    private static final HashBiMap<Key, Reference<? extends SynchronizedPersistentDataStorage>> storages = HashBiMap.create();

    static {
        var databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        database = databaseProvider.database("persistent_data");
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataSet.class, new HandlerSet());
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataClearSet.class, new HandlerClearSet());
        PacketAPI.instance().registerHandler(PacketWrapperNodeDataRemove.class, new HandlerRemove());
        PacketAPI.instance().registerHandler(PacketWrapperNodeQuery.class, new HandlerQuery());
        new CollectorHandler();
    }

    public static SynchronizedPersistentDataStorage storage(Key key) {
        lock.lock();
        Reference<? extends SynchronizedPersistentDataStorage> ref;
        ref = storages.getOrDefault(key, null);
        var storage = ref == null ? null : ref.get();
        if (storage == null) {
            storage = new SynchronizedPersistentDataStorage(key);
            var doc = database.get(key.value());
            if (doc != null) {
                database.delete(key.value());
                database.insert(key.toString(), doc);
                var object = gson.fromJson(doc.serializeToString(), JsonObject.class);
                storage.loadFromJsonObject(object);
            } else {
                doc = database.get(key.toString());
                if (doc != null) {
                    var object = gson.fromJson(doc.serializeToString(), JsonObject.class);
                    storage.loadFromJsonObject(object);
                }
            }
            ref = new SoftReference<>(storage);
            storages.put(key, ref);
        }
        lock.unlock();
        return storage;
    }

    public static void init() {
    }

    public static void exit() {
    }

    private static class CollectorHandler extends Thread {
        public CollectorHandler() {
            setName("SynchronizedStorageGCHandler");
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            // noinspection InfiniteLoopStatement
            while (true) {
                try {
                    var reference = queue.remove();
                    lock.lock();
                    storages.inverse().remove(reference);
                    lock.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
