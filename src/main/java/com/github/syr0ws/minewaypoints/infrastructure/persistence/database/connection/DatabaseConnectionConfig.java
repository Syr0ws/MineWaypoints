package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.DatabaseDriver;

public class DatabaseConnectionConfig {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final DatabaseDriver driver;

    public DatabaseConnectionConfig(String host, int port, String database, String username, String password, DatabaseDriver driver) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.driver = driver;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public DatabaseDriver getDriver() {
        return this.driver;
    }
}
