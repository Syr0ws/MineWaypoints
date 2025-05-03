package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.impl;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.plugin.Plugin;

public class SQLiteDatabaseConnection extends HikariDatabaseConnection {

    public static final String DATABASE_FILE = "database.db";

    private final Plugin plugin;

    public SQLiteDatabaseConnection(Plugin plugin, DatabaseConnectionConfig config) {
        super(config);

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        this.plugin = plugin;
    }

    @Override
    protected String getUrl(DatabaseConnectionConfig config) {
        String path = this.plugin.getDataFolder().getAbsolutePath() + "/" + DATABASE_FILE;
        return "jdbc:sqlite:" + path;
    }

    @Override
    protected void enhanceHikariConfig(HikariConfig config) {
        config.setConnectionInitSql("PRAGMA foreign_keys = ON;");
    }
}
