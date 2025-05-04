package com.github.syr0ws.minewaypoints.infrastructure.persistence.database;

public enum DatabaseDriver {

    SQLITE("sqlite", "org.sqlite.JDBC"),
    MYSQL("mysql", "com.mysql.cj.jdbc.Driver"),
    MARIADB("mariadb", "org.mariadb.jdbc.Driver"),
    POSTGRESQL("postgresql", "org.postgresql.Driver");

    private final String driverName;
    private final String driverClass;

    DatabaseDriver(String driverName, String driverClass) {
        this.driverName = driverName;
        this.driverClass = driverClass;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public String getDriverClass() {
        return this.driverClass;
    }
}
