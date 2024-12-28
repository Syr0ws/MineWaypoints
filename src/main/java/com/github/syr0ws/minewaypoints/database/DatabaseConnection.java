package com.github.syr0ws.minewaypoints.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {

    void openConnection(DatabaseConnectionConfig config) throws SQLException;

    void closeConnection() throws SQLException;

    boolean isClosed();

    Connection getConnection();
}
