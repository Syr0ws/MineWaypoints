package com.github.syr0ws.minewaypoints.database;

import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnection;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {

    private final Plugin plugin;
    private final DatabaseConnection connection;

    public DatabaseInitializer(Plugin plugin, DatabaseConnection connection) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(connection == null) {
            throw new IllegalArgumentException("connection cannot be null");
        }

        this.plugin = plugin;
        this.connection = connection;
    }

    public void init() throws SQLException {

        Connection connection = this.connection.getConnection();

        InputStream stream = this.plugin.getResource("sql/init.sql");

        boolean autoCommit = connection.getAutoCommit();

        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setSendFullScript(false);
        scriptRunner.setStopOnError(true);
        scriptRunner.setLogWriter(null);
        scriptRunner.runScript(new InputStreamReader(stream));

        // ScriptRunner changes the auto commit state of the connection.
        // This is to restore its state after the script execution.
        connection.setAutoCommit(autoCommit);
    }
}
