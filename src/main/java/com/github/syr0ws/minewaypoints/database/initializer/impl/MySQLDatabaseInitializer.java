package com.github.syr0ws.minewaypoints.database.initializer.impl;

import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionConfig;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MySQLDatabaseInitializer extends RemoteDatabaseInitializer {

    public MySQLDatabaseInitializer(Plugin plugin, DatabaseConnectionConfig config) {
        super(plugin, config);
    }

    @Override
    public void init() throws Exception {

        DatabaseConnectionConfig config = super.getConfig();
        String query = String.format("CREATE DATABASE IF NOT EXISTS %s;", config.getDatabase());

        try(Connection connection = super.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.execute();

            super.init();
        }
    }
}
