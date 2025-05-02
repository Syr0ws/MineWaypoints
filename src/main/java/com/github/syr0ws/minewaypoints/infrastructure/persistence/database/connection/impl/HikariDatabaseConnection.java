package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.impl;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.DatabaseDriver;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnection;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class HikariDatabaseConnection implements DatabaseConnection {

    private final DatabaseConnectionConfig config;
    protected HikariDataSource dataSource;

    public HikariDatabaseConnection(DatabaseConnectionConfig config) {

        if (config == null) {
            throw new IllegalArgumentException("config cannot be null");
        }

        this.config = config;
    }

    protected abstract String getUrl(DatabaseConnectionConfig config);

    protected void enhanceHikariConfig(HikariConfig config) {
    }

    @Override
    public void open() {

        if (!this.isClosed()) {
            throw new IllegalStateException("Connection already open");
        }

        DatabaseDriver driver = this.config.getDriver();
        String url = this.getUrl(this.config);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driver.getDriverClass());
        hikariConfig.setUsername(this.config.getUsername());
        hikariConfig.setPassword(this.config.getPassword());
        hikariConfig.setConnectionTimeout(10000);
        hikariConfig.setAutoCommit(true);

        this.enhanceHikariConfig(hikariConfig);

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void close() throws SQLException {

        if (this.isClosed()) {
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

        if (this.isClosed()) {
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

    protected DatabaseConnectionConfig getConfig() {
        return this.config;
    }
}
