package com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.impl;

import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.DatabaseDriver;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;

public class CommonDatabaseConnection extends HikariDatabaseConnection {

    public CommonDatabaseConnection(DatabaseConnectionConfig config) {
        super(config);
    }

    @Override
    protected String getUrl(DatabaseConnectionConfig config) {
        DatabaseDriver driver = config.getDriver();
        return String.format("jdbc:%s://%s:%d/%s?tcpKeepAlive=true",
                driver.getDriverName(),
                config.getHost(),
                config.getPort(),
                config.getDatabase()
        );
    }
}
