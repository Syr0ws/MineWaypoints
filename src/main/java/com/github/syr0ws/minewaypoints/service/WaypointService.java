package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.util.Promise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaypointService {

    Promise<Waypoint> createWaypoint(UUID ownerId, String name, Material icon, Location location);

    Promise<Void> updateWaypointIcon(long waypointId, Material icon);

    Promise<Void> updateWaypointName(long waypointId, String newName);

    Promise<Void> updateWaypointLocation(long waypointId, Location location);

    Promise<Void> deleteWaypoint(long waypointId);

    Promise<WaypointShare> shareWaypoint(String targetName, long waypointId);

    Promise<Boolean> unshareWaypoint(String targetName, long waypointId);

    Promise<List<WaypointShare>> getSharedWaypoints(UUID userId);

    Promise<List<WaypointShare>> getSharedWith(long waypointId);

    Promise<Void> activateWaypoint(UUID userId, long waypointId);

    Promise<Void> deactivateWaypoint(UUID userId, long waypointId);

    Promise<Optional<Waypoint>> loadActivatedWaypoint(UUID userId, World world);
}
