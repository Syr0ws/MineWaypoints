package com.github.syr0ws.minewaypoints.plugin.business.service.impl;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.business.failure.NoWaypointAccess;
import com.github.syr0ws.minewaypoints.plugin.business.failure.WaypointNotFound;
import com.github.syr0ws.minewaypoints.plugin.business.service.BusinessWaypointActivationService;
import com.github.syr0ws.minewaypoints.plugin.persistence.WaypointDAO;
import com.github.syr0ws.minewaypoints.plugin.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointEntity;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class SimpleBusinessWaypointActivationService implements BusinessWaypointActivationService {

    private final WaypointDAO waypointDAO;

    public SimpleBusinessWaypointActivationService(WaypointDAO waypointDAO) {
        Validate.notNull(waypointDAO, "waypointDAO cannot be null");
        this.waypointDAO = waypointDAO;
    }

    @Override
    public BusinessResult<Waypoint, BusinessFailure> activateWaypoint(UUID playerId, long waypointId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");

        // Retrieving the waypoint.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypointById(waypointId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotFound(waypointId));
        }

        WaypointEntity waypoint = optional.get();

        // Checking that the player has access to the waypoint.
        boolean hasAccess = this.waypointDAO.hasAccessToWaypoint(playerId, waypointId);

        if (!hasAccess) {
            return BusinessResult.error(new NoWaypointAccess(playerId, waypointId));
        }

        // At most one waypoint can be activated for a player in a world.
        // Here, we deactivate any other activated waypoint for the player in the given world.
        this.waypointDAO.deactivateWaypoint(playerId, waypoint.getLocation().getWorld());

        // Activating the waypoint.
        this.waypointDAO.activateWaypoint(waypointId, playerId);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Waypoint, BusinessFailure> deactivateWaypoint(UUID playerId, long waypointId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");

        Optional<WaypointEntity> optional = this.waypointDAO.findWaypointById(waypointId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotFound(waypointId));
        }

        WaypointEntity waypoint = optional.get();

        // Checking that the player has access to the waypoint.
        boolean hasAccess = this.waypointDAO.hasAccessToWaypoint(playerId, waypointId);

        if (!hasAccess) {
            return BusinessResult.error(new NoWaypointAccess(playerId, waypointId));
        }

        this.waypointDAO.deactivateWaypoint(waypointId, playerId);

        return BusinessResult.success(waypoint);
    }

    @Override
    public Optional<Waypoint> getActivatedWaypoint(UUID playerId, String world) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        Validate.notEmpty(world, "world cannot be null or empty");
        return this.waypointDAO.findActivatedWaypoint(playerId, world).map(entity -> entity);
    }

    @Override
    public Set<Long> getActivatedWaypointIds(UUID playerId) throws WaypointDataException {
        return this.waypointDAO.getActivatedWaypointIds(playerId);
    }
}
