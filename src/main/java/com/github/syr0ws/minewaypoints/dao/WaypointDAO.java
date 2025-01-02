package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.*;
import org.bukkit.Material;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaypointDAO {

    WaypointModel createWaypoint(WaypointUserModel owner, String name, Material icon, WaypointLocation location) throws WaypointDataException;

    void updateWaypoint(WaypointModel waypoint) throws WaypointDataException;

    void deleteWaypoint(long waypointId) throws WaypointDataException;

    Optional<WaypointModel> findWaypoint(long waypointId) throws WaypointDataException;

    WaypointShareModel shareWaypoint(WaypointUserModel to, long waypointId) throws WaypointDataException;

    void unshareWaypoint(WaypointUserModel from,  long waypointId) throws WaypointDataException;

    List<WaypointModel> findWaypoints(UUID userId) throws WaypointDataException;

    List<WaypointShareModel> findSharedWaypoints(UUID userId) throws WaypointDataException;
}
