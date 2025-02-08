package com.github.syr0ws.minewaypoints.database.connection;

import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.minewaypoints.database.DatabaseDriver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class DatabaseConnectionLoader {

    private final Plugin plugin;

    public DatabaseConnectionLoader(Plugin plugin) {

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        this.plugin = plugin;
    }

    public DatabaseConnectionConfig loadConfig() throws ConfigurationException {

        FileConfiguration config = this.plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("database");

        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);

        String database = section.getString("database", "minewaypoints");
        String username = section.getString("username", "root");
        String password = section.getString("password", "");

        DatabaseDriver driver = this.loadDriver(section);

        return new DatabaseConnectionConfig(host, port, database, username, password, driver);
    }

    private DatabaseDriver loadDriver(ConfigurationSection section) throws ConfigurationException {

        String driverName = section.getString("driver", "");

        return Arrays.stream(DatabaseDriver.values())
                .filter(driver -> driver.getDriverName().equalsIgnoreCase(driverName))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException(String.format("Property '%s' at '%s.driver' is invalid", driverName, section.getCurrentPath())));
    }
}
