package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointShareEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;
import org.bukkit.Material;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WaypointDAO {

    WaypointEntity createWaypoint(WaypointUser owner, String name, String icon, WaypointLocation location) throws WaypointDataException;

    void updateWaypoint(WaypointEntity waypoint) throws WaypointDataException;

    void deleteWaypoint(long waypointId) throws WaypointDataException;

    boolean hasWaypointByName(UUID ownerId, String name) throws WaypointDataException;

    Optional<WaypointEntity> findWaypointByOwnerAndId(UUID ownerId, long waypointId) throws WaypointDataException;

    Optional<WaypointEntity> findWaypointByOwnerAndName(UUID ownerId, String name) throws WaypointDataException;

    Optional<WaypointEntity> findWaypoint(long waypointId) throws WaypointDataException;

    WaypointShareEntity shareWaypoint(long waypointId, UUID targetId) throws WaypointDataException;

    boolean unshareWaypoint(long waypointId, UUID targetId) throws WaypointDataException;

    boolean isShared(String username, long waypointId) throws WaypointDataException;

    boolean isShared(long waypointId, UUID targetId) throws WaypointDataException;

    void activateWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    void deactivateWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    void deactivateWaypoint(UUID playerId, String world) throws WaypointDataException;

    List<WaypointEntity> findWaypoints(UUID ownerId) throws WaypointDataException;

    Optional<WaypointShare> findWaypointShare(String userName, long waypointId) throws WaypointDataException;

    List<WaypointShareEntity> findSharedWaypoints(UUID userId) throws WaypointDataException;

    List<WaypointShareEntity> findSharedWith(WaypointEntity waypoint) throws WaypointDataException;

    boolean hasAccessToWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    boolean isWaypointOwner(UUID playerId, long waypointId) throws WaypointDataException;

    boolean isActivated(UUID playerId, long waypointId) throws WaypointDataException;

    Optional<WaypointEntity> findActivatedWaypoint(UUID playerId, String world) throws WaypointDataException;

    Set<Long> getActivatedWaypointIds(UUID playerId) throws WaypointDataException;
}
