package com.github.syr0ws.minewaypoints.model;

public class WaypointLocation {

    private final String world;
    private final double x, y, z;

    public WaypointLocation(String world, double x, double y, double z) {

        if (world == null || world.isEmpty()) {
            throw new IllegalArgumentException("world cannot be null or empty");
        }

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
