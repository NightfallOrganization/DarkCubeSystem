/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi;

import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;

import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.darkcube.system.util.data.PersistentDataStorage;

class NodeDataSaver {
    private final Database database;
    private final ConcurrentMap<UUID, Document> dataToSave = new ConcurrentHashMap<>();
    // Writes should be fast so if this queue gets filled with more than 256 entries, we can just let new entries wait
    private final BlockingDeque<UUID> saveQueue = new LinkedBlockingDeque<>(256);
    private final PersistentDataStorage.UpdateNotifier saveNotifier = storage -> save((UserLocalPersistentDataStorage) storage);
    private Task<?> task;
    private volatile boolean exit = false;

    public NodeDataSaver(Database database) {
        this.database = database;
    }

    public void exit() {
        exit = true;
        saveQueue.offer(UUID.randomUUID());
        task.join();
    }

    public void init() {
        task = Task.supply(() -> {
            while (!exit) {
                try {
                    var uniqueId = saveQueue.take();
                    var data = dataToSave.remove(uniqueId);
                    if (data == null) continue;
                    database.insert(uniqueId.toString(), data);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    public PersistentDataStorage.UpdateNotifier saveNotifier() {
        return saveNotifier;
    }

    void save(UserLocalPersistentDataStorage storage) {
        var data = Document.newJsonDocument();
        data.append("name", storage.name());
        data.append("uuid", storage.uniqueId());
        var json = storage.storeToJsonObject().toString();
        data.append("persistentData", DocumentFactory.json().parse(json));
        save(storage.uniqueId(), data);
    }

    private void save(UUID uniqueId, Document document) {
        dataToSave.put(uniqueId, document); // Overwrite old entry to prevent clogging memory and/or the database
        saveQueue.offer(uniqueId);
    }
}
