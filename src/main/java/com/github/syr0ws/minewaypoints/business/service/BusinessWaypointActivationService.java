package com.github.syr0ws.minewaypoints.business.service;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BusinessWaypointActivationService {

    BusinessResult<Waypoint, BusinessFailure> activateWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    BusinessResult<Waypoint, BusinessFailure> deactivateWaypoint(UUID playerId, long waypointId) throws WaypointDataException;

    Optional<Waypoint> getActivatedWaypoint(UUID playerId, String world) throws WaypointDataException;

    Set<Long> getActivatedWaypointIds(UUID playerId) throws WaypointDataException;
}
