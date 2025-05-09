package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.impl;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.impl.SQLiteDatabaseConnection;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabaseInitializer extends AbstractDatabaseInitializer {

    public SQLiteDatabaseInitializer(Plugin plugin, DatabaseConnectionConfig config) {
        super(plugin, config);
    }

    @Override
    protected Connection getConnection(boolean database) throws SQLException {
        Plugin plugin = super.getPlugin();
        String path = plugin.getDataFolder().getAbsolutePath() + "/" + SQLiteDatabaseConnection.DATABASE_FILE;
        return DriverManager.getConnection("jdbc:sqlite:" + path);
    }
}
