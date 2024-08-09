package eu.darkcube.system.impl.standalone.util.data.database;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class FlatfileConnectionFactory {
    private final Path file;
    private NonCloseableConnection connection;

    public FlatfileConnectionFactory(Path file) {
        this.file = file;
    }

    public abstract String implementationName();

    public abstract Connection createConnection(Path file) throws SQLException;

    public synchronized Connection getConnection() throws SQLException {
        var connection = this.connection;
        if (connection == null || connection.isClosed()) {
            connection = new NonCloseableConnection(createConnection(file));
            this.connection = connection;
        }
        return connection;
    }

    public void shutdown() throws Exception {
        if (this.connection != null) {
            this.connection.shutdown();
        }
    }
}
