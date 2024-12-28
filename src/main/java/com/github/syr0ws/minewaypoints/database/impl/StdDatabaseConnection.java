package com.github.syr0ws.minewaypoints.database.impl;

import com.github.syr0ws.minewaypoints.database.DatabaseConnection;
import com.github.syr0ws.minewaypoints.database.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.database.DatabaseDriver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class StdDatabaseConnection implements DatabaseConnection {

    private HikariDataSource dataSource;

    @Override
    public void openConnection(DatabaseConnectionConfig config) {

        DatabaseDriver driver = config.getDriver();

        String url = String.format("jdbc:%s://%s:%d/%s?tcpKeepAlive=true",
                driver.getDriverName(),
                config.getHost(),
                config.getPort(),
                config.getDatabase()
        );

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driver.getDriverClass());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setAutoCommit(false);

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void closeConnection() {
        this.dataSource.close();
    }

    @Override
    public boolean isClosed() {
        return this.dataSource.isClosed();
    }

    @Override
    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
