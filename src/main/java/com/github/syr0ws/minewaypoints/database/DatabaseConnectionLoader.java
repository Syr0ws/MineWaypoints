package com.github.syr0ws.minewaypoints.database;

import com.github.syr0ws.minewaypoints.exception.ConfigurationException;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
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

    public Database loadConnection() throws ConfigurationException {

        FileConfiguration config = this.plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("database");

        // Loading properties.
        String host = section.getString("host", "localhost");
        String port = section.getString("port", "3306");
        String database = section.getString("database", "minewaypoints");
        String username = section.getString("username", "root");
        String password = section.getString("password", "");

        DatabaseDriver driver = this.loadDriver(section);

        // Configuring the data source.
        DataSourceConfig dsConfig = new DataSourceConfig();
        dsConfig.setUrl(String.format("jdbc:%s://%s:%s/%s", driver.getDriverName(), host, port, database));
        dsConfig.setUsername(username);
        dsConfig.setPassword(password);
        dsConfig.setDriver(driver.getDriverClass());

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setName("default");
        dbConfig.setDataSourceConfig(dsConfig);

        // Creating the database.
        try {
            return DatabaseFactory.create(dbConfig);
        } catch (Exception exception) {
            throw new ConfigurationException("Failed to create database connection", exception);
        }
    }

    private DatabaseDriver loadDriver(ConfigurationSection section) throws ConfigurationException {

        String driverName = section.getString("driver");

        if (driverName == null) {
            throw new ConfigurationException(String.format("Property '%s.driver' cannot be null", section.getCurrentPath()));
        }

        return Arrays.stream(DatabaseDriver.values())
                .filter(driver -> driver.getDriverName().equalsIgnoreCase(driverName))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException(String.format("Property '%s' at '%s.driver' is invalid", driverName, section.getCurrentPath())));
    }
}
