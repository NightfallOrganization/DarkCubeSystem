package eu.darkcube.system.impl.standalone.util.data.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.h2.jdbc.JdbcConnection;

public class H2ConnectionFactory extends FlatfileConnectionFactory {
    public H2ConnectionFactory(Path file) {
        super(file);
    }

    @Override
    public String implementationName() {
        return "h2";
    }

    @Override
    public Connection createConnection(Path file) throws SQLException {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new SQLException("Failed to create database", e);
        }
        return new JdbcConnection("jdbc:h2:" + file.toAbsolutePath(), new Properties(), null, null, false);
    }
}
