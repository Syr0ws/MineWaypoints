package com.github.syr0ws.minewaypoints.database.impl;

import com.github.syr0ws.minewaypoints.database.DatabaseConnection;
import com.github.syr0ws.minewaypoints.database.DatabaseConnectionConfig;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabaseConnection implements DatabaseConnection {

    private final Plugin plugin;
    private Connection connection;

    public SQLiteDatabaseConnection(Plugin plugin) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        this.plugin = plugin;
    }

    @Override
    public void openConnection(DatabaseConnectionConfig config) throws SQLException {

        String path = this.plugin.getDataFolder().getAbsolutePath() + "/database.db";
        String url = "jdbc:sqlite:" + path;

        this.connection = DriverManager.getConnection(url);
    }

    @Override
    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    @Override
    public boolean isClosed() {
        try {
            return this.connection == null || this.connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
