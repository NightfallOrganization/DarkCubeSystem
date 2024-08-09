package eu.darkcube.system.impl.standalone.userapi;

import java.util.UUID;
import java.util.function.Function;

import eu.darkcube.system.impl.standalone.util.data.StandalonePersistentDataStorage;
import eu.darkcube.system.impl.standalone.util.data.StorageImplementation;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.WrapperPersistentDataStorage;

public class UserStandalonePersistentDataStorage extends WrapperPersistentDataStorage {
    private final UUID uniqueId;
    private String name;

    public UserStandalonePersistentDataStorage(PersistentDataStorage handle, UUID uniqueId, String name) {
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

    public static class Implementation implements StorageImplementation<UserStandalonePersistentDataStorage> {

        @Override
        public Function<JsonObject, JsonObject> saver(UserStandalonePersistentDataStorage wrapped) {
            return json -> {
                var j = new JsonObject();
                j.addProperty("name", wrapped.name());
                j.addProperty("uuid", wrapped.uniqueId().toString());
                j.add("persistentData", json);
                return j;
            };
        }

        @Override
        public Pair<UserStandalonePersistentDataStorage, @Nullable JsonObject> wrapper(StandalonePersistentDataStorage storage, @Nullable JsonObject json) {
            var uniqueId = UUID.fromString(storage.key().value());
            var name = json == null ? null : json.get("name").getAsString();
            if (name == null) name = uniqueId.toString().substring(0, 16);
            var persistentData = json == null ? null : json.get("persistentData").getAsJsonObject();

            var userStorage = new UserStandalonePersistentDataStorage(storage, uniqueId, name);
            return new Pair(userStorage, persistentData);
        }
    }
}
