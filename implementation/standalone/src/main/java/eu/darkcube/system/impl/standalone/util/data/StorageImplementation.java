package eu.darkcube.system.impl.standalone.util.data;

import java.util.function.Function;

import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.PersistentDataStorage;

public interface StorageImplementation<T extends PersistentDataStorage> {
    Function<JsonObject, JsonObject> saver(T wrapped);

    Pair<T, @Nullable JsonObject> wrapper(StandalonePersistentDataStorage storage, @Nullable JsonObject json);

    record Pair<T, V>(T first, V second) {
    }

    class Default implements StorageImplementation<StandalonePersistentDataStorage> {
        @Override
        public Function<JsonObject, JsonObject> saver(StandalonePersistentDataStorage wrapped) {
            return d -> d;
        }

        @Override
        public Pair<StandalonePersistentDataStorage, @Nullable JsonObject> wrapper(StandalonePersistentDataStorage storage, @Nullable JsonObject json) {
            return new Pair(storage, json);
        }
    }
}
