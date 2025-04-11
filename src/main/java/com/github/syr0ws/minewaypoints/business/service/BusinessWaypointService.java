package com.github.syr0ws.minewaypoints.business.service;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointSharingRequest;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessWaypointService {

    BusinessResult<Waypoint, BusinessFailure> createWaypoint(UUID ownerId, String name, String icon, Location location) throws WaypointDataException;

    BusinessResult<Waypoint, BusinessFailure> updateWaypoint(UUID ownerId, long waypointId, String newWaypointName, WaypointLocation newLocation, String newIcon) throws WaypointDataException;

    BusinessResult<Waypoint, BusinessFailure> deleteWaypoint(UUID ownerId, long waypointId) throws WaypointDataException;

    BusinessResult<WaypointShare, BusinessFailure> unshareWaypointByOwner(UUID ownerId, long waypointId, UUID targetId) throws WaypointDataException;

    BusinessResult<WaypointShare, BusinessFailure> unshareWaypointBySharedWith(long waypointId, UUID targetId) throws WaypointDataException;

    Optional<Waypoint> getWaypointById(long waypointId) throws WaypointDataException;

    Optional<Waypoint> getWaypointByNameAndOwner(String waypointName, UUID ownerId) throws WaypointDataException;

    Optional<WaypointShare> getWaypointShare(long waypointId, UUID playerId) throws WaypointDataException;

    List<WaypointShare> getSharedWaypoints(UUID userId) throws WaypointDataException;

    List<WaypointShare> getSharedWith(long waypointId) throws WaypointDataException;

    BusinessResult<WaypointSharingRequest, BusinessFailure> createWaypointSharingRequest(UUID ownerId, String waypointName, UUID targetId) throws WaypointDataException;

    BusinessResult<WaypointShare, BusinessFailure> acceptWaypointSharingRequest(UUID requestId) throws WaypointDataException;

    BusinessResult<WaypointSharingRequest, BusinessFailure> cancelWaypointSharingRequest(UUID requestId);
}
