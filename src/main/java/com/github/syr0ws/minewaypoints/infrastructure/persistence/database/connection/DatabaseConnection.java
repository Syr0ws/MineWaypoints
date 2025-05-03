package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.DatabaseDriver;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {

    void open() throws SQLException;

    void close() throws SQLException;

    boolean isClosed();

    Connection getConnection();

    DatabaseDriver getDriver();
}
