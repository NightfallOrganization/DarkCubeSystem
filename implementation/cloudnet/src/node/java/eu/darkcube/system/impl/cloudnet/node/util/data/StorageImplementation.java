/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.util.data;

import java.util.function.Function;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.util.data.PersistentDataStorage;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public interface StorageImplementation<T extends PersistentDataStorage> {

    Function<Document, Document> saver(T wrapped);

    Tuple2<T, Document> wrapper(SynchronizedPersistentDataStorage storage, Document document);

    class Default implements StorageImplementation<SynchronizedPersistentDataStorage> {
        @Override
        public Function<Document, Document> saver(SynchronizedPersistentDataStorage wrapped) {
            return d -> d;
        }

        @Override
        public Tuple2<SynchronizedPersistentDataStorage, Document> wrapper(SynchronizedPersistentDataStorage storage, Document document) {
            return Tuple.of(storage, document);
        }
    }
}
