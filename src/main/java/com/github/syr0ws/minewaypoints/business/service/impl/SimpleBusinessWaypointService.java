package com.github.syr0ws.minewaypoints.business.service.impl;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.failure.*;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointService;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointShareEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SimpleBusinessWaypointService implements BusinessWaypointService {

    private final WaypointDAO waypointDAO;
    private final WaypointUserDAO waypointUserDAO;

    public SimpleBusinessWaypointService(WaypointDAO waypointDAO, WaypointUserDAO waypointUserDAO) {
        Validate.notNull(waypointDAO, "waypointDAO cannot be null");
        Validate.notNull(waypointUserDAO, "waypointUserDAO cannot be null");

        this.waypointDAO = waypointDAO;
        this.waypointUserDAO = waypointUserDAO;
    }

    @Override
    public BusinessResult<Waypoint, ? extends BusinessFailure> createWaypoint(UUID ownerId, String name, String icon, Location location) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notEmpty(name, "name cannot be null or empty");
        Validate.notEmpty(icon, "icon cannot be null or empty");
        Validate.notNull(location, "location cannot be null");

        // Checking waypoint name.
        if(!this.isValidWaypointName(name)) {
            return BusinessResult.error(new InvalidWaypointName(name));
        }

        // Retrieving user's data.
        Optional<WaypointUserEntity> optional = this.waypointUserDAO.findUser(ownerId);

        if(optional.isEmpty()) {
            return BusinessResult.error(new WaypointGenericBusinessFailure());
        }

        WaypointUser owner = optional.get();

        // Checking that the user does not already have a waypoint with the same name.
        boolean hasWaypointByName = this.waypointDAO.hasWaypointByName(ownerId, name);

        if(hasWaypointByName) {
            return BusinessResult.error(new WaypointNameAlreadyExists(name));
        }

        // Creating the waypoint.
        WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
        WaypointEntity waypoint = this.waypointDAO.createWaypoint(owner, name, icon, waypointLocation);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Waypoint, ? extends BusinessFailure> updateWaypointNameByName(UUID ownerId, String waypointName, String newName) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notEmpty(newName, "newName cannot be null or empty");

        // Checking waypoint name.
        if(!this.isValidWaypointName(newName)) {
            return BusinessResult.error(new InvalidWaypointName(newName));
        }

        // Checking that the user has a waypoint with the specified name.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypointByOwnerAndName(ownerId, waypointName);

        if(optional.isEmpty()) {
            return BusinessResult.error(new WaypointNameNotFound(waypointName));
        }

        // Checking that the user does not already have a waypoint with the same new name.
        boolean hasWaypointByName = this.waypointDAO.hasWaypointByName(ownerId, newName);

        if(hasWaypointByName) {
            return BusinessResult.error(new WaypointNameAlreadyExists(newName));
        }

        // Updating the name of the waypoint.
        WaypointEntity waypoint = optional.get();
        waypoint.setName(newName);

        this.waypointDAO.updateWaypoint(waypoint);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Waypoint, ? extends BusinessFailure> updateWaypointIconById(UUID ownerId, long waypointId, String icon) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notEmpty(icon, "icon cannot be null or empty");

        // Checking that the waypoint exists.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

        if(optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotFound(waypointId));
        }

        WaypointEntity waypoint = optional.get();

        // Checking that the user is the owner of the waypoint.
        if(!waypoint.getOwner().getId().equals(ownerId)) {
            return BusinessResult.error(new WaypointNotFound(waypointId));
        }

        // Updating the icon of the waypoint.
        waypoint.setIcon(icon);

        this.waypointDAO.updateWaypoint(waypoint);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Void, ? extends BusinessFailure> deleteWaypoint(UUID ownerId, long waypointId) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");

        // Checking that the waypoint exists.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

        if(optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotFound(waypointId));
        }

        WaypointEntity waypoint = optional.get();

        // Checking that the user is the owner of the waypoint.
        if(!waypoint.getOwner().getId().equals(ownerId)) {
            return BusinessResult.error(new WaypointNotFound(waypointId));
        }

        this.waypointDAO.deleteWaypoint(waypointId);

        return BusinessResult.success(null);
    }

    @Override
    public BusinessResult<WaypointShare, ? extends BusinessFailure> shareWaypoint(UUID ownerId, long waypointId, String targetName) {
        return null;
    }

    @Override
    public BusinessResult<Void, ? extends BusinessFailure> unshareWaypoint(UUID userId, long waypointId) throws WaypointDataException {
        return null;
    }

    @Override
    public BusinessResult<List<WaypointShare>, ? extends BusinessFailure> getSharedWaypoints(UUID userId) throws WaypointDataException {
        Validate.notNull(userId, "userId cannot be null");

        // Retrieving the shares associated with the user.
        List<WaypointShare> sharedWaypoints = this.waypointDAO.findSharedWaypoints(userId).stream()
                .map(waypointShareEntity -> (WaypointShare) waypointShareEntity)
                .collect(Collectors.toCollection(ArrayList::new)); // Keeping the list mutable.

        return BusinessResult.success(sharedWaypoints);
    }

    @Override
    public BusinessResult<List<WaypointShare>, ? extends BusinessFailure> getSharedWith(long waypointId) throws WaypointDataException {

        // Checking that the waypoint exists.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

        if(optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotFound(waypointId));
        }

        WaypointEntity waypoint = optional.get();

        // Retrieving the shares associated with the waypoint.
        List<WaypointShare> shares = this.waypointDAO.findSharedWith(waypoint).stream()
                .map(waypointShareEntity -> (WaypointShare) waypointShareEntity)
                .collect(Collectors.toCollection(ArrayList::new)); // Keeping the list mutable.

        return BusinessResult.success(shares);
    }

    private boolean isValidWaypointName(String waypointName) {
        return waypointName != null && !waypointName.isEmpty() && waypointName.length() <= 32;
    }
}
