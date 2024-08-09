package eu.darkcube.system.impl.standalone.util.data;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import eu.darkcube.system.impl.standalone.userapi.UserStandalonePersistentDataStorage;
import eu.darkcube.system.impl.standalone.util.data.database.H2ConnectionFactory;
import eu.darkcube.system.impl.standalone.util.data.database.SQLDatabase;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;
import eu.darkcube.system.util.data.PersistentDataStorage;

public abstract class StandaloneCustomPersistentDataProvider implements CustomPersistentDataProvider {
    private static final Map<String, Function<PersistentDataStorage, StorageImplementation<?>>> STORAGE_IMPLEMENTATIONS = new HashMap<>();
    private final ReferenceQueue<StandalonePersistentDataStorage> queue = new ReferenceQueue<>();
    private final Lock lock = new ReentrantLock(false);
    private final Map<String, Map<Key, StorageReference>> storages = new HashMap<>();
    private final Map<String, Map<Key, PersistentDataStorage>> wrappers = new HashMap<>();
    private SQLDatabase database;

    static {
        STORAGE_IMPLEMENTATIONS.put("userapi_users", _ -> new UserStandalonePersistentDataStorage.Implementation());
    }

    public static String toString(Key key) {
        if (key.namespace().isEmpty()) return key.value();
        return key.asString();
    }

    private StorageImplementation.Pair<StandalonePersistentDataStorage, PersistentDataStorage> storagePair(String table, Key key) {
        lock.lock();
        try {
            @Nullable var map = storages.get(table);
            @Nullable var ref = map == null ? null : map.getOrDefault(key, null);
            var storage = ref == null ? null : ref.get();
            if (storage == null) {
                var pair = load(table, key);
                storage = pair.first();
                var wrapped = pair.second();
                ref = new StorageReference(storage, queue, table, key);
                storages.computeIfAbsent(table, _ -> new HashMap<>()).put(key, ref);
                wrappers.computeIfAbsent(table, _ -> new HashMap<>()).put(key, wrapped);
                return pair;
            }
            var wrapper = wrappers.get(table).get(key);
            return new StorageImplementation.Pair<>(storage, wrapper);
        } finally {
            lock.unlock();
        }
    }

    private StorageImplementation.Pair<StandalonePersistentDataStorage, PersistentDataStorage> load(String table, Key key) {
        var toString = toString(key);
        if (!database.contains(table)) {
            database.create(table);
        }
        var json = loadJson(database, table, toString);
        var storage = new StandalonePersistentDataStorage(database, table, key);
        var pair = setupStorage(storage, table, json);
        var persistentData = pair.second();
        if (persistentData != null) {
            storage.loadFromJsonObject(persistentData);
        }
        var wrapper = pair.first();
        return new StorageImplementation.Pair<>(storage, wrapper);
    }

    private static @Nullable JsonObject loadJson(SQLDatabase database, String table, String path) {
        return database.get(table, path);
    }

    private static <T extends PersistentDataStorage> StorageImplementation.Pair<T, JsonObject> setupStorage(StandalonePersistentDataStorage storage, String table, @Nullable JsonObject json) {
        var implementation = (StorageImplementation<T>) findImplementation(table, storage);
        var wrapped = implementation.wrapper(storage, json);
        storage.documentSaver = implementation.saver(wrapped.first());
        return wrapped;
    }

    private static StorageImplementation<?> findImplementation(String table, StandalonePersistentDataStorage storage) {
        var implementation = STORAGE_IMPLEMENTATIONS.get(table);
        if (implementation == null) {
            implementation = _ -> new StorageImplementation.Default();
        }
        return implementation.apply(storage);
    }

    public StandaloneCustomPersistentDataProvider() {
        new CollectorThread().start();
    }

    public void init() throws SQLException, IOException {
        this.database = new SQLDatabase(new H2ConnectionFactory(dataDirectory().resolve("database-h2")));
        this.database.init();
    }

    public void shutdown() {
        this.database.shutdown();
    }

    @Override
    public @NotNull PersistentDataStorage persistentData(@NotNull String table, @NotNull Key key) {
        return storagePair(table, key).second();
    }

    protected abstract Path dataDirectory();

    private static class StorageReference extends SoftReference<StandalonePersistentDataStorage> {
        private final String table;
        private final Key key;

        public StorageReference(StandalonePersistentDataStorage referent, ReferenceQueue<? super StandalonePersistentDataStorage> q, String table, Key key) {
            super(referent, q);
            this.table = table;
            this.key = key;
        }
    }

    private class CollectorThread extends Thread {
        public CollectorThread() {
            setName("StandaloneStorageGCHandler");
            setDaemon(true);
        }

        @Override
        public void run() {
            // noinspection InfiniteLoopStatement
            while (true) {
                try {
                    var reference = (StorageReference) queue.remove();
                    lock.lock();
                    try {
                        var map = storages.get(reference.table);
                        map.remove(reference.key);

                        wrappers.get(reference.table).remove(reference.key);
                        if (map.isEmpty()) {
                            storages.remove(reference.table);
                            wrappers.remove(reference.table);
                        }
                    } finally {
                        lock.unlock();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
