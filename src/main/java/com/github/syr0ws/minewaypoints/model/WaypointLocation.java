package com.github.syr0ws.minewaypoints.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WaypointLocation {

    private final String world;
    private final double x, y, z;

    public WaypointLocation(String world, double x, double y, double z) {

        if(world == null || world.isEmpty()) {
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

    public Location toLocation() {

        World world = Bukkit.getWorld(this.world);

        if(world == null) {
            throw new IllegalArgumentException(String.format("World '%s' does not exist", this.world));
        }

        return new Location(world, this.x, this.y, this.z);
    }

    public static WaypointLocation fromLocation(Location location) {
        return new WaypointLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }
}
