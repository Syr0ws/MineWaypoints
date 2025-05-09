package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.impl;

import com.github.syr0ws.crafter.sql.ScriptRunner;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.DatabaseDriver;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.DatabaseInitializer;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractDatabaseInitializer implements DatabaseInitializer {

    private static final String INIT_SCRIPT = "init.sql";

    private final Plugin plugin;
    private final DatabaseConnectionConfig config;

    public AbstractDatabaseInitializer(Plugin plugin, DatabaseConnectionConfig config) {

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        this.plugin = plugin;
        this.config = config;
    }

    protected abstract Connection getConnection(boolean database) throws Exception;

    @Override
    public void init() throws Exception {

        DatabaseDriver driver = this.config.getDriver();

        String initScriptResource = String.format("database/%s/%s", driver.getDriverName(), INIT_SCRIPT);

        try (Connection connection = this.getConnection(true);
             InputStream stream = this.plugin.getResource(initScriptResource)) {

            if (stream == null) {
                throw new NullPointerException(String.format("Resource '%s' not found", initScriptResource));
            }

            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.setStopOnError(true);
            scriptRunner.runScript(new InputStreamReader(stream));
        }
    }

    protected Plugin getPlugin() {
        return this.plugin;
    }

    protected DatabaseConnectionConfig getConfig() {
        return this.config;
    }
}
