package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Mapper {

    public static WaypointLocation toWaypointLocation(Location location) {
        return new WaypointLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    }

    public static Location toLocation(WaypointLocation location) {

        World world = Bukkit.getWorld(location.getWorld());

        if (world == null) {
            throw new IllegalArgumentException(String.format("World '%s' does not exist", location.getWorld()));
        }

        return new Location(world, location.getX(), location.getY(), location.getZ());
    }
}
