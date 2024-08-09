package eu.darkcube.system.impl.standalone.util.data.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.AsyncExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLDatabase {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLDatabase.class);
    private static final String TABLE_METADATA = "metadata";
    private static final int VERSION = 1;
    private final FlatfileConnectionFactory connectionFactory;
    private final Gson gson = new Gson();

    public SQLDatabase(FlatfileConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void init() {
        var metadataExists = contains(TABLE_METADATA);
        if (!metadataExists) {
            create("metadata");
            metaSetVersion(VERSION);
        } else {
            var version = metaGetVersion();
            // Future migration code
        }
    }

    public void shutdown() {
        try {
            this.connectionFactory.shutdown();
        } catch (Exception e) {
            LOGGER.error("Exception whilst disabling SQLDatabase", e);
        }
    }

    private void metaSetVersion(int version) {
        set(TABLE_METADATA, "version", Integer.toString(version));
    }

    private int metaGetVersion() {
        var string = getRaw(TABLE_METADATA, "version");
        if (string == null) return Integer.MIN_VALUE;
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            LOGGER.error("Illegal stored version: {}", string, e);
            return -1;
        }
    }

    private static boolean tableExists(Connection connection, String table) throws SQLException {
        try (var rs = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null)) {
            while (rs.next()) {
                if (rs.getString(3).equalsIgnoreCase(table)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final String CREATE = "CREATE TABLE IF NOT EXISTS `%s` (`Key` VARCHAR(64) PRIMARY KEY, `Value` TEXT)";
    private static final String GET = "SELECT `Value` FROM `%s` WHERE `Key` = ?";
    private static final String INSERT = "INSERT INTO `%s` (`Key`,`Value`) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE `%s` SET `Value` = ? WHERE `Key` = ?";
    private static final String DELETE = "DELETE FROM `%s` WHERE `Key` = ?";

    public boolean contains(String table) {
        try (var connection = connectionFactory.getConnection()) {
            return tableExists(connection, table);
        } catch (SQLException e) {
            LOGGER.error("Failed to check if table {} exists", table, e);
        }
        return false;
    }

    public void create(String table) {
        try (var connection = connectionFactory.getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.execute(CREATE.formatted(table));
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to create table {}", table, e);
        }
    }

    public boolean delete(String table, String path) {
        try (var connection = connectionFactory.getConnection()) {
            try (var statement = connection.prepareStatement(DELETE.formatted(table))) {
                statement.setString(1, path);
                return statement.execute();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to delete path {} in {}", path, table, e);
        }
        return false;
    }

    public @Nullable JsonObject get(String table, String path) {
        var string = getRaw(table, path);
        if (string == null) return null;
        return gson.fromJson(string, JsonObject.class);
    }

    public boolean set(String table, String path, JsonObject data) {
        var string = gson.toJson(data);
        return set(table, path, string);
    }

    private @Nullable String getRaw(String table, String path) {
        try (var connection = connectionFactory.getConnection()) {
            try (var statement = connection.prepareStatement(GET.formatted(table))) {
                statement.setString(1, path);
                try (var rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("Value");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to query {} from {}", path, table, e);
        }
        return null;
    }

    private boolean set(String table, String path, String data) {
        try (var connection = connectionFactory.getConnection()) {
            synchronized (this) {
                boolean contains;
                try (var statement = connection.prepareStatement(GET.formatted(table))) {
                    statement.setString(1, path);
                    try (var rs = statement.executeQuery()) {
                        contains = rs.next();
                    }
                }
                if (contains) {
                    try (var statement = connection.prepareStatement(UPDATE.formatted(table))) {
                        statement.setString(1, data);
                        statement.setString(2, path);
                        return statement.execute();
                    }
                } else {
                    try (var statement = connection.prepareStatement(INSERT.formatted(table))) {
                        statement.setString(1, path);
                        statement.setString(2, data);
                        return statement.execute();
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to save data for key {} in table {}", path, table, e);
        }
        return false;
    }

    public CompletableFuture<Boolean> setAsync(String table, String path, JsonObject data) {
        return CompletableFuture.supplyAsync(() -> set(table, path, data), AsyncExecutor.cachedService());
    }
}
