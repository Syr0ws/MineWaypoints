package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.impl.MySQLDatabaseInitializer;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.impl.PostgresDatabaseInitializer;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.impl.SQLiteDatabaseInitializer;
import org.bukkit.plugin.Plugin;

public class DatabaseInitializerFactory {

    public static DatabaseInitializer getInitializer(Plugin plugin, DatabaseConnectionConfig config) {

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        return switch (config.getDriver()) {
            case SQLITE -> new SQLiteDatabaseInitializer(plugin, config);
            case MYSQL -> new MySQLDatabaseInitializer(plugin, config);
            case POSTGRESQL -> new PostgresDatabaseInitializer(plugin, config);
        };
    }
}
