package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.util.Callback;
import com.github.syr0ws.minewaypoints.util.Promise;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public interface WaypointService {

    Promise<Waypoint> createWaypoint(UUID ownerId, String name, Material icon, Location location);

    Promise<Void> updateWaypoint(Waypoint waypoint);

    Promise<Void> deleteWaypoint(long waypointId);

    Promise<WaypointShare> shareWaypoint(WaypointUser user, long waypointId);

    Promise<Void> unshareWaypoint(WaypointUser user, long waypointId);
}
