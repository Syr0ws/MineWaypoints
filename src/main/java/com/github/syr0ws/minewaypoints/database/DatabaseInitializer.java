package com.github.syr0ws.minewaypoints.database;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

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

    public void init() {

        Connection connection = this.connection.getConnection();

        InputStream stream = this.plugin.getResource("sql/init.sql");

        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setSendFullScript(false);
        scriptRunner.setStopOnError(true);
        scriptRunner.setLogWriter(null);
        scriptRunner.runScript(new InputStreamReader(stream));
    }
}
