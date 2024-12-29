package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.util.Callback;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

public interface WaypointService {

    Waypoint createWaypoint(UUID ownerId, String name, Material icon, Location location) throws WaypointDataException;

    void createWaypointAsync(UUID ownerId, String name, Material icon, Location location, Callback<Waypoint> callback);

    WaypointShare shareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException;

    void shareWaypointAsync(WaypointUser user, long waypointId, Callback<WaypointShare> callback);

    void unshareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException;

    void unshareWaypointAsync(WaypointUser user, long waypointId, Callback<Void> callback);
}
