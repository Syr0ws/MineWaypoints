package com.github.syr0ws.minewaypoints.database.connection.impl;

import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.database.DatabaseDriver;
import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class CommonDatabaseConnection implements DatabaseConnection {

    private final DatabaseConnectionConfig config;
    private HikariDataSource dataSource;

    public CommonDatabaseConnection(DatabaseConnectionConfig config) {

        if(config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        this.config = config;
    }

    @Override
    public void open() {

        if(!this.isClosed()) {
            throw new IllegalStateException("Connection already open");
        }

        DatabaseDriver driver = this.config.getDriver();

        String url = String.format("jdbc:%s://%s:%d/%s?tcpKeepAlive=true",
                driver.getDriverName(),
                this.config.getHost(),
                this.config.getPort(),
                this.config.getDatabase()
        );

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driver.getDriverClass());
        hikariConfig.setUsername(this.config.getUsername());
        hikariConfig.setPassword(this.config.getPassword());
        hikariConfig.setAutoCommit(true);

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void close() throws SQLException {

        if(this.isClosed()) {
            throw new SQLException("Connection already closed");
        }

        this.dataSource.close();
    }

    @Override
    public boolean isClosed() {
        return this.dataSource == null || this.dataSource.isClosed();
    }

    @Override
    public Connection getConnection() {

        if(this.isClosed()) {
            throw new IllegalStateException("Connection closed");
        }

        try {
            return this.dataSource.getConnection();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public DatabaseDriver getDriver() {
        return this.config.getDriver();
    }
}
