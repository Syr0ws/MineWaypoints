package com.github.syr0ws.minewaypoints.database.connection;

import com.github.syr0ws.minewaypoints.database.DatabaseDriver;
import com.github.syr0ws.minewaypoints.database.connection.impl.CommonDatabaseConnection;
import com.github.syr0ws.minewaypoints.database.connection.impl.SQLiteDatabaseConnection;
import org.bukkit.plugin.Plugin;

public class DatabaseConnectionFactory {

    private final Plugin plugin;

    public DatabaseConnectionFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    public DatabaseConnection createDatabaseConnection(DatabaseConnectionConfig config) {

        DatabaseDriver driver = config.getDriver();

        if(driver == DatabaseDriver.SQLITE) {
            return new SQLiteDatabaseConnection(this.plugin, config);
        } else {
            return new CommonDatabaseConnection(config);
        }
    }
}
