package com.github.syr0ws.minewaypoints.platform;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface BukkitWaypointService {

    Promise<Waypoint> createWaypoint(Player owner, String name, Material icon, Location location);

    Promise<Waypoint> updateWaypointNameByName(Player owner, String waypointName, String newName);

    Promise<Waypoint> updateWaypointIconById(Player owner, long waypointId, Material icon);

    Promise<Void> deleteWaypoint(Player owner, long waypointId);

    void shareWaypoint(UUID ownerId, long waypointId, String targetName);

    void unshareWaypoint(UUID userId, long waypointId);

    void getSharedWaypoints(UUID userId);

    void getSharedWith(long waypointId);
}
