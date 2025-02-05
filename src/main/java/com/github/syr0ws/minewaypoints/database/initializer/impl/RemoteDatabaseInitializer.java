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
    protected Connection getConnection(boolean database) throws SQLException {

        DatabaseConnectionConfig config = super.getConfig();

        String baseUrl = String.format("jdbc:%s://%s:%d/",
                config.getDriver().getDriverName(),
                config.getHost(),
                config.getPort());

        String url = database ? baseUrl + config.getDatabase() : baseUrl;

        return DriverManager.getConnection(url, config.getUsername(), config.getPassword());
    }
}
