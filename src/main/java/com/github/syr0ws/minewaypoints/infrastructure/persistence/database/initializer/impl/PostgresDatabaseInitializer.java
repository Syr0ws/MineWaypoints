package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.impl;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgresDatabaseInitializer extends RemoteDatabaseInitializer {

    public PostgresDatabaseInitializer(Plugin plugin, DatabaseConnectionConfig config) {
        super(plugin, config);
    }

    @Override
    @SuppressWarnings("all")
    public void init() throws Exception {

        DatabaseConnectionConfig config = super.getConfig();

        // Load the Driver. If not done, the driver cannot be found.
        Class.forName("org.postgresql.Driver");

        // Checking if the database exists and creating it if it is not the case.
        String query = "SELECT 1 FROM pg_database WHERE datname = ?;";

        try (Connection connection = super.getConnection(false);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, config.getDatabase());
            ResultSet result = statement.executeQuery();

            if (!result.next()) {
                try (Statement createStatement = connection.createStatement()) {
                    createStatement.executeUpdate(String.format("CREATE DATABASE %s;", config.getDatabase()));
                }
            }

            super.init();
        }
    }
}
