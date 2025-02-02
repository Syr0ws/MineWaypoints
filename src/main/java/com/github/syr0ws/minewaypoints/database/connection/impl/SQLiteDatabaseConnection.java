package com.github.syr0ws.minewaypoints.database.connection.impl;

import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.database.DatabaseDriver;
import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnection;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabaseConnection implements DatabaseConnection {

    private static final String DATABASE_FILE = "database.db";

    private final Plugin plugin;
    private final DatabaseConnectionConfig config;
    private Connection connection;

    public SQLiteDatabaseConnection(Plugin plugin, DatabaseConnectionConfig config) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void open() throws SQLException {

        if(!this.isClosed()) {
            throw new IllegalStateException("Connection already open");
        }

        String path = this.plugin.getDataFolder().getAbsolutePath() + "/" + DATABASE_FILE;
        String url = "jdbc:sqlite:" + path;

        this.connection = DriverManager.getConnection(url);
        this.connection.setAutoCommit(true);
    }

    @Override
    public void close() throws SQLException {

        if(this.isClosed()) {
            throw new IllegalStateException("Connection already closed");
        }

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

        if(this.isClosed()) {
            throw new IllegalStateException("Connection closed");
        }

        return this.connection;
    }

    @Override
    public DatabaseDriver getDriver() {
        return this.config.getDriver();
    }
}
