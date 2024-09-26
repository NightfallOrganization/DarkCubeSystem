/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.util.data;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;
import eu.darkcube.system.util.data.PersistentDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrapperCustomPersistentDataProvider implements CustomPersistentDataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(WrapperCustomPersistentDataProvider.class);
    private final ReferenceQueue<SynchronizedPersistentDataStorage> queue = new ReferenceQueue<>();
    private final Map<String, Map<Key, WeakStorageReference>> storages = new HashMap<>();

    public WrapperCustomPersistentDataProvider() {
        Executors.newSingleThreadExecutor(runnable -> Thread.ofVirtual().name("SynchronizedPersistentDataStorageCleaner").unstarted(runnable)).submit(() -> {
            // noinspection InfiniteLoopStatement
            while (true) {
                var ref = (WeakStorageReference) queue.remove();
                synchronized (storages) {
                    var byKey = storages.get(ref.table);
                    LOGGER.debug("Unloaded storage table {} key {}", ref.table, ref.key);
                    if (byKey != null) {
                        byKey.remove(ref.key, ref);
                        if (byKey.isEmpty()) {
                            storages.remove(ref.table, byKey);
                        }
                    }
                }
            }
        });
        InjectionLayer.boot().instance(EventManager.class).registerListener(new Listener());
    }

    public class Listener {
        @EventListener
        public void handle(ChannelMessageReceiveEvent event) {
            if (!event.channel().equals(SynchronizedPersistentDataStorage.CHANNEL)) return;
            if (event.message().equals("update-data")) {
                updateData(event.content());
            } else {
                LOGGER.error("Unknown message {}", event.message());
            }
        }

        private void updateData(DataBuf buf) {
            var table = buf.readString();
            var key = buf.readObject(Key.class);
            var storage = loadedStorage(table, key);
            var state = buf.readInt();
            if (storage == null) {
                LOGGER.info("Table {} key {} storage not found for update to {}", table, key, state);
                return;
            }
            var newData = buf.readObject(JsonObject.class);
            storage.updateData(state, newData);
        }
    }

    @Override
    public @NotNull PersistentDataStorage persistentData(@NotNull String table, @NotNull Key key) {
        synchronized (storages) {
            var storage = loadedStorage(table, key);
            if (storage != null) return storage;
            storage = new SynchronizedPersistentDataStorage(table, key);
            var ref = new WeakStorageReference(storage);
            storages.computeIfAbsent(table, _ -> new HashMap<>()).put(key, ref);
            return storage;
        }
    }

    private @Nullable SynchronizedPersistentDataStorage loadedStorage(String table, Key key) {
        synchronized (storages) {
            var byKey = storages.get(table);
            if (byKey == null) return null;
            var ref = byKey.get(key);
            if (ref == null) return null;
            var storage = ref.get();
            if (storage == null) {
                LOGGER.debug("Purge storage table {} key {}", ref.table, ref.key);
                byKey.remove(key);
                if (byKey.isEmpty()) {
                    storages.remove(table);
                }
                return null;
            }
            return storage;
        }
    }

    private class WeakStorageReference extends WeakReference<SynchronizedPersistentDataStorage> {
        private final String table;
        private final Key key;

        public WeakStorageReference(SynchronizedPersistentDataStorage referent) {
            super(referent, queue);
            this.table = referent.table();
            this.key = referent.key();
        }
    }
}
