package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.util.Promise;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public interface WaypointService {

    Promise<Waypoint> createWaypoint(UUID ownerId, String name, Material icon, Location location);

    Promise<Void> updateWaypointIcon(long waypointId, Material icon);

    Promise<Void> updateWaypointName(long waypointId, String newName);

    Promise<Void> updateWaypointLocation(long waypointId, Location location);

    Promise<Void> deleteWaypoint(long waypointId);

    Promise<WaypointShare> shareWaypoint(UUID userId, long waypointId);

    Promise<Void> unshareWaypoint(UUID userId, long waypointId);
}
