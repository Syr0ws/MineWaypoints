package com.github.syr0ws.minewaypoints.database.initializer.impl;

import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionConfig;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RemoteDatabaseInitializer extends AbstractDatabaseInitializer {

    public RemoteDatabaseInitializer(Plugin plugin, DatabaseConnectionConfig config) {
        super(plugin, config);
    }

    @Override
    protected Connection getConnection() throws SQLException {
        DatabaseConnectionConfig config = super.getConfig();
        return DriverManager.getConnection(
                String.format("jdbc:%s://%s:%d", config.getDriver().getDriverName(), config.getHost(), config.getPort()),
                config.getUsername(),
                config.getPassword()
        );
    }
}
