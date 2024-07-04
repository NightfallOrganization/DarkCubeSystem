/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.userapi;

import java.util.UUID;
import java.util.function.Function;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.impl.cloudnet.node.util.data.StorageImplementation;
import eu.darkcube.system.impl.cloudnet.node.util.data.SynchronizedPersistentDataStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.WrapperPersistentDataStorage;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class UserNodePersistentDataStorage extends WrapperPersistentDataStorage {
    private final UUID uniqueId;
    private String name;

    public UserNodePersistentDataStorage(SynchronizedPersistentDataStorage handle, UUID uniqueId, String name) {
        super(handle);
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public static class Implementation implements StorageImplementation<UserNodePersistentDataStorage> {
        @Override
        public Function<Document, Document> saver(UserNodePersistentDataStorage wrapped) {
            return document -> {
                var d = Document.newJsonDocument();
                d.append("name", wrapped.name());
                d.append("uuid", wrapped.uniqueId());
                d.append("persistentData", document);
                return d;
            };
        }

        @Override
        public Tuple2<UserNodePersistentDataStorage, @Nullable Document> wrapper(SynchronizedPersistentDataStorage storage, @Nullable Document document) {
            var uniqueId = UUID.fromString(storage.key().value());
            var name = document == null ? null : document.getString("name");
            if (name == null) name = uniqueId.toString().substring(0, 16);
            var persistentData = document == null ? null : document.readDocument("persistentData");

            var userStorage = new UserNodePersistentDataStorage(storage, uniqueId, name);
            return Tuple.of(userStorage, persistentData);
        }
    }
}
