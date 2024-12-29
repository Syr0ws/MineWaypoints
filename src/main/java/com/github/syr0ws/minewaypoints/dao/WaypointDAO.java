package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public interface WaypointDAO {

    Waypoint createWaypoint(WaypointUser owner, String name, Material icon, WaypointLocation location) throws WaypointDataException;

    Waypoint findWaypoint(long waypointId) throws WaypointDataException;

    void updateWaypoint(Waypoint waypoint) throws WaypointDataException;

    WaypointShare shareWaypoint(WaypointUser to, long waypointId) throws WaypointDataException;

    void unshareWaypoint(WaypointUser from,  long waypointId) throws WaypointDataException;

    List<Waypoint> findWaypoints(UUID userId) throws WaypointDataException;

    List<WaypointShare> findSharedWaypoints(UUID userId) throws WaypointDataException;
}
