package com.github.syr0ws.minewaypoints.plugin.persistence;

import com.github.syr0ws.minewaypoints.plugin.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointLocation;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointUser;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointShareEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WaypointDAO {

    WaypointEntity createWaypoint(WaypointUser owner, String name, String icon, WaypointLocation location) throws WaypointDataException;

    void updateWaypoint(WaypointEntity waypoint) throws WaypointDataException;

    void deleteWaypoint(long waypointId) throws WaypointDataException;

    boolean hasWaypointByName(UUID ownerId, String waypointName) throws WaypointDataException;

    boolean hasAccessToWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    int countWaypoints(UUID ownerId) throws WaypointDataException;

    Optional<WaypointEntity> findWaypointById(long waypointId) throws WaypointDataException;

    Optional<WaypointEntity> findWaypointByOwnerAndId(UUID ownerId, long waypointId) throws WaypointDataException;

    Optional<WaypointEntity> findWaypointByOwnerAndName(UUID ownerId, String waypointName) throws WaypointDataException;

    List<WaypointEntity> findWaypoints(UUID ownerId) throws WaypointDataException;

    WaypointShareEntity shareWaypoint(long waypointId, UUID targetId) throws WaypointDataException;

    boolean unshareWaypoint(long waypointId, UUID targetId) throws WaypointDataException;

    boolean isShared(long waypointId, String username) throws WaypointDataException;

    boolean isShared(long waypointId, UUID targetId) throws WaypointDataException;

    Optional<WaypointShareEntity> findWaypointShare(long waypointId, UUID playerId) throws WaypointDataException;

    List<WaypointShareEntity> findSharedWaypoints(UUID userId) throws WaypointDataException;

    List<WaypointShareEntity> findSharedWith(long waypointId) throws WaypointDataException;

    void activateWaypoint(long waypointId, UUID playerId) throws WaypointDataException;

    void deactivateWaypoint(long waypointId, UUID playerId) throws WaypointDataException;

    void deactivateWaypoint(UUID playerId, String world) throws WaypointDataException;

    Optional<WaypointEntity> findActivatedWaypoint(UUID playerId, String world) throws WaypointDataException;

    Set<Long> getActivatedWaypointIds(UUID playerId) throws WaypointDataException;
}
