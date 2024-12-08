package com.github.syr0ws.minewaypoints.util;

public enum Permission {

    COMMAND_WAYPOINTS("command.waypoints");

    private static final String PERMISSION_PREFIX = "minewaypoints";

    private final String name;

    Permission(String name) {
        this.name = String.format("%s.%s", PERMISSION_PREFIX, name);
    }

    public String getName() {
        return this.name;
    }
}
