package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointShareEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;
import org.bukkit.Material;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaypointDAO {

    WaypointEntity createWaypoint(WaypointOwnerEntity owner, String name, Material icon, WaypointLocation location) throws WaypointDataException;

    void updateWaypoint(WaypointEntity waypoint) throws WaypointDataException;

    void deleteWaypoint(long waypointId) throws WaypointDataException;

    boolean hasWaypointByName(UUID ownerId, String name) throws WaypointDataException;

    Optional<WaypointEntity> findWaypoint(long waypointId) throws WaypointDataException;

    WaypointShareEntity shareWaypoint(WaypointUserEntity to, WaypointEntity waypoint) throws WaypointDataException;

    boolean unshareWaypoint(String username, long waypointId) throws WaypointDataException;

    void activateWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    void deactivateWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    void deactivateWaypoint(UUID playerId, String world) throws WaypointDataException;

    List<WaypointEntity> findWaypoints(UUID ownerId) throws WaypointDataException;

    Optional<WaypointShare> findWaypointShare(String userName, long waypointId) throws WaypointDataException;

    List<WaypointShareEntity> findSharedWaypoints(UUID userId) throws WaypointDataException;

    List<WaypointShareEntity> findSharedWith(WaypointEntity waypoint) throws WaypointDataException;

    boolean hasAccessToWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    boolean isActivated(UUID playerId, long waypointId) throws WaypointDataException;

    Optional<WaypointEntity> findActivatedWaypoint(UUID playerId, String world) throws WaypointDataException;
}
