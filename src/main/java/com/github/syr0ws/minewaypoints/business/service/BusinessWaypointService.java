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
import java.util.UUID;

public interface BusinessWaypointService {

    BusinessResult<Waypoint, BusinessFailure> createWaypoint(UUID ownerId, String name, String icon, Location location) throws WaypointDataException;

    BusinessResult<Waypoint, BusinessFailure> updateWaypointNameByName(UUID ownerId, String waypointName, String newName) throws WaypointDataException;

    BusinessResult<Waypoint, BusinessFailure> updateWaypointLocationByName(UUID ownerId, String waypointName, WaypointLocation location) throws WaypointDataException;

    BusinessResult<Waypoint, BusinessFailure> updateWaypointIconById(UUID ownerId, long waypointId, String icon) throws WaypointDataException;

    BusinessResult<Void, BusinessFailure> deleteWaypoint(UUID ownerId, long waypointId) throws WaypointDataException;

    BusinessResult<Void, BusinessFailure> unshareWaypointByOwner(UUID ownerId, long waypointId, UUID targetId) throws WaypointDataException;

    BusinessResult<List<WaypointShare>, BusinessFailure> getSharedWaypoints(UUID userId) throws WaypointDataException;

    BusinessResult<List<WaypointShare>, BusinessFailure> getSharedWith(long waypointId) throws WaypointDataException;

    BusinessResult<WaypointSharingRequest, BusinessFailure> createWaypointSharingRequest(UUID ownerId, String waypointName, UUID targetId) throws WaypointDataException;

    BusinessResult<WaypointShare, BusinessFailure> acceptWaypointSharingRequest(UUID requestId) throws WaypointDataException;

    BusinessResult<WaypointSharingRequest, BusinessFailure> cancelWaypointSharingRequest(UUID requestId);
}
