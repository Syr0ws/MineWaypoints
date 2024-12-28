package com.github.syr0ws.minewaypoints.database;

import com.github.syr0ws.minewaypoints.database.impl.SQLiteDatabaseConnection;
import com.github.syr0ws.minewaypoints.database.impl.StdDatabaseConnection;
import org.bukkit.plugin.Plugin;

public class DatabaseConnectionFactory {

    private final Plugin plugin;

    public DatabaseConnectionFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    public DatabaseConnection createDatabaseConnection(DatabaseDriver driver) {

        if(driver == DatabaseDriver.SQLITE) {
            return new SQLiteDatabaseConnection(this.plugin);
        } else {
            return new StdDatabaseConnection();
        }
    }
}
