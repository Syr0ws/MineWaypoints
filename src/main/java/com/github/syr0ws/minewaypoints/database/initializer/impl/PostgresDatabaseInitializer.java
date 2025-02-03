package com.github.syr0ws.minewaypoints.database.initializer.impl;

import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionConfig;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PostgresDatabaseInitializer extends RemoteDatabaseInitializer {

    public PostgresDatabaseInitializer(Plugin plugin, DatabaseConnectionConfig config) {
        super(plugin, config);
    }

    @Override
    public void init() throws Exception {

        DatabaseConnectionConfig config = super.getConfig();

        String query = """
            DO $$
            BEGIN
                IF NOT EXISTS (SELECT FROM pg_database WHERE datname = ?)
                THEN
                    EXECUTE format('CREATE DATABASE %I', ?);
                END IF;
            END $$;
        """;

        try(Connection connection = super.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, config.getDatabase());
            statement.setString(2, config.getDatabase());
            statement.execute();

            super.init();
        }
    }
}
