package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.service.util.WaypointEnums;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public interface WaypointService {

    Promise<Waypoint> createWaypoint(UUID ownerId, String name, Material icon, Location location);

    Promise<Void> updateWaypointIcon(long waypointId, Material icon);

    Promise<Void> updateWaypointName(long waypointId, String newName);

    Promise<Void> updateWaypointLocation(long waypointId, Location location);

    Promise<Boolean> deleteWaypoint(long waypointId);

    Promise<WaypointEnums.WaypointShareStatus> shareWaypoint(String targetName, long waypointId);

    Promise<Boolean> unshareWaypoint(String targetName, long waypointId);

    Promise<Boolean> isWaypointSharedWith(String targetName, long waypointId);

    Promise<List<WaypointShare>> getSharedWaypoints(UUID playerId);

    Promise<List<WaypointShare>> getSharedWith(long waypointId);
}
