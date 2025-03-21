package com.github.syr0ws.minewaypoints.business.service.impl;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.failure.*;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointService;
import com.github.syr0ws.minewaypoints.business.settings.WaypointSettings;
import com.github.syr0ws.minewaypoints.cache.WaypointSharingRequestCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.*;
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
    private final WaypointSharingRequestCache sharingRequestCache;
    private final WaypointSettings settings;

    public SimpleBusinessWaypointService(WaypointDAO waypointDAO, WaypointUserDAO waypointUserDAO, WaypointSharingRequestCache sharingRequestCache, WaypointSettings settings) {
        Validate.notNull(waypointDAO, "waypointDAO cannot be null");
        Validate.notNull(waypointUserDAO, "waypointUserDAO cannot be null");
        Validate.notNull(sharingRequestCache, "sharingRequestCache cannot be null");
        Validate.notNull(settings, "settings cannot be null");

        this.waypointDAO = waypointDAO;
        this.waypointUserDAO = waypointUserDAO;
        this.sharingRequestCache = sharingRequestCache;
        this.settings = settings;
    }

    @Override
    public BusinessResult<Waypoint, BusinessFailure> createWaypoint(UUID ownerId, String name, String icon, Location location) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notEmpty(name, "name cannot be null or empty");
        Validate.notEmpty(icon, "icon cannot be null or empty");
        Validate.notNull(location, "location cannot be null");

        // Checking that the user has not reached the maximum number of waypoints.
        if (this.settings.hasWaypointLimit()) {

            int waypointCount = this.waypointDAO.countWaypoints(ownerId);

            if (waypointCount >= this.settings.waypointLimit()) {
                return BusinessResult.error(new WaypointLimitReached());
            }
        }

        // Checking waypoint name.
        if (!this.isValidWaypointName(name)) {
            return BusinessResult.error(new InvalidWaypointName(name));
        }

        // Retrieving user's data.
        Optional<WaypointUserEntity> optional = this.waypointUserDAO.findUserById(ownerId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointGenericBusinessFailure());
        }

        WaypointUser owner = optional.get();

        // Checking that the user does not already have a waypoint with the same name.
        boolean hasWaypointByName = this.waypointDAO.hasWaypointByName(ownerId, name);

        if (hasWaypointByName) {
            return BusinessResult.error(new WaypointNameAlreadyExists(name));
        }

        // Creating the waypoint.
        WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
        WaypointEntity waypoint = this.waypointDAO.createWaypoint(owner, name, icon, waypointLocation);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Waypoint, BusinessFailure> updateWaypointName(UUID ownerId, String waypointName, String newName) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notEmpty(newName, "newName cannot be null or empty");

        // Checking waypoint name.
        if (!this.isValidWaypointName(newName)) {
            return BusinessResult.error(new InvalidWaypointName(newName));
        }

        // Checking that the user has a waypoint with the specified name.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypointByOwnerAndName(ownerId, waypointName);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointNameNotFound(waypointName));
        }

        // Checking that the user does not already have a waypoint with the same new name.
        boolean hasWaypointByName = this.waypointDAO.hasWaypointByName(ownerId, newName);

        if (hasWaypointByName) {
            return BusinessResult.error(new WaypointNameAlreadyExists(newName));
        }

        // Updating the name of the waypoint.
        WaypointEntity waypoint = optional.get();
        waypoint.setName(newName);

        this.waypointDAO.updateWaypoint(waypoint);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Waypoint, BusinessFailure> updateWaypointLocation(UUID ownerId, String waypointName, WaypointLocation location) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notNull(location, "waypoint cannot be null");

        // Checking that the user has a waypoint with the specified name.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypointByOwnerAndName(ownerId, waypointName);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointNameNotFound(waypointName));
        }

        WaypointEntity waypoint = optional.get();

        // The world of the waypoint cannot be changed.
        String oldWorld = waypoint.getLocation().getWorld();
        String newWorld = location.getWorld();

        if(!oldWorld.equals(newWorld)) {
            return BusinessResult.error(new InvalidWaypointWorld(newWorld));
        }

        // Updating the location of the waypoint.
        waypoint.setLocation(location);

        this.waypointDAO.updateWaypoint(waypoint);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Waypoint, BusinessFailure> updateWaypointIcon(UUID ownerId, long waypointId, String icon) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notEmpty(icon, "icon cannot be null or empty");

        // Checking that the waypoint exists.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypointById(waypointId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotOwned(waypointId));
        }

        WaypointEntity waypoint = optional.get();

        // Checking that the user is the owner of the waypoint.
        if (!waypoint.getOwner().getId().equals(ownerId)) {
            return BusinessResult.error(new WaypointNotOwned(waypointId));
        }

        // Updating the icon of the waypoint.
        waypoint.setIcon(icon);

        this.waypointDAO.updateWaypoint(waypoint);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<Waypoint, BusinessFailure> deleteWaypoint(UUID ownerId, long waypointId) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");

        // Checking that the waypoint exists.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypointById(waypointId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotOwned(waypointId));
        }

        WaypointEntity waypoint = optional.get();

        // Checking that the user is the owner of the waypoint.
        if (!waypoint.getOwner().getId().equals(ownerId)) {
            return BusinessResult.error(new WaypointNotOwned(waypointId));
        }

        this.waypointDAO.deleteWaypoint(waypointId);

        return BusinessResult.success(waypoint);
    }

    @Override
    public BusinessResult<WaypointShare, BusinessFailure> unshareWaypointByOwner(UUID ownerId, long waypointId, UUID targetId) throws WaypointDataException {

        // Retrieving the waypoint.
        Optional<WaypointEntity> waypointOptional = this.waypointDAO.findWaypointById(waypointId);

        if (waypointOptional.isEmpty()) {
            return BusinessResult.error(new WaypointNotOwned(waypointId));
        }

        WaypointEntity waypoint = waypointOptional.get();

        // Checking that the user is the owner of the waypoint.
        if (!waypoint.getOwner().getId().equals(ownerId)) {
            return BusinessResult.error(new WaypointNotOwned(waypointId));
        }

        // Retrieving the target user data.
        Optional<WaypointUserEntity> targetUserOptional = this.waypointUserDAO.findUserById(targetId);

        if (targetUserOptional.isEmpty()) {
            return BusinessResult.error(new TargetUserNotFound(targetId));
        }

        WaypointUserEntity target = targetUserOptional.get();

        // Retrieving the share.
        Optional<WaypointShareEntity> shareOptional = this.waypointDAO.findWaypointShare(waypointId, targetId);

        if (shareOptional.isEmpty()) {
            return BusinessResult.error(new WaypointNotSharedWithTarget(waypoint, target));
        }

        WaypointShare share = shareOptional.get();

        // Unsharing the waypoint with the target.
        this.waypointDAO.unshareWaypoint(waypoint.getId(), targetId);

        return BusinessResult.success(share);
    }

    @Override
    public BusinessResult<WaypointShare, BusinessFailure> unshareWaypointBySharedWith(long waypointId, UUID targetId) throws WaypointDataException {
        Validate.notNull(targetId, "targetId cannot be null");

        // Retrieving the share.
        Optional<WaypointShareEntity> optional = this.waypointDAO.findWaypointShare(waypointId, targetId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new WaypointNotShared(waypointId));
        }

        WaypointShare share = optional.get();
        this.waypointDAO.unshareWaypoint(waypointId, targetId);

        return BusinessResult.success(share);
    }

    @Override
    public List<WaypointShare> getSharedWaypoints(UUID userId) throws WaypointDataException {
        Validate.notNull(userId, "userId cannot be null");
        // Retrieving the shares associated with the user.
        return this.waypointDAO.findSharedWaypoints(userId).stream()
                .map(waypointShareEntity -> (WaypointShare) waypointShareEntity)
                .collect(Collectors.toCollection(ArrayList::new)); // Keeping the list mutable.
    }

    @Override
    public List<WaypointShare> getSharedWith(long waypointId) throws WaypointDataException {
        // Retrieving the shares associated with the waypoint.
        return this.waypointDAO.findSharedWith(waypointId).stream()
                .map(waypointShareEntity -> (WaypointShare) waypointShareEntity)
                .collect(Collectors.toCollection(ArrayList::new)); // Keeping the list mutable.
    }

    @Override
    public BusinessResult<WaypointSharingRequest, BusinessFailure> createWaypointSharingRequest(UUID ownerId, String waypointName, UUID targetId) throws WaypointDataException {

        // Retrieving the waypoint.
        Optional<WaypointEntity> waypointOptional = this.waypointDAO.findWaypointByOwnerAndName(ownerId, waypointName);

        if (waypointOptional.isEmpty()) {
            return BusinessResult.error(new WaypointNameNotFound(waypointName));
        }

        WaypointEntity waypoint = waypointOptional.get();

        // Retrieving the target user data.
        Optional<WaypointUserEntity> targetUserOptional = this.waypointUserDAO.findUserById(targetId);

        if (targetUserOptional.isEmpty()) {
            return BusinessResult.error(new TargetUserNotFound(targetId));
        }

        WaypointUser target = targetUserOptional.get();

        // Checking that the target user is not the owner of the waypoint.
        if (waypoint.getOwner().getId().equals(targetId)) {
            return BusinessResult.error(new SharingRequestToOwner(waypoint, target));
        }

        // Checking that the waypoint is not already shared with the target.
        boolean isShared = this.waypointDAO.isShared(waypoint.getId(), targetId);

        if (isShared) {
            return BusinessResult.error(new WaypointAlreadyShared(waypoint, target));
        }

        WaypointSharingRequest request = new WaypointSharingRequest(UUID.randomUUID(), waypoint, target, System.currentTimeMillis());
        this.sharingRequestCache.addSharingRequest(request);

        return BusinessResult.success(request);
    }

    @Override
    public BusinessResult<WaypointShare, BusinessFailure> acceptWaypointSharingRequest(UUID requestId) throws WaypointDataException {

        // Retrieving the sharing request.
        Optional<WaypointSharingRequest> optional = this.sharingRequestCache.getSharingRequest(requestId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new SharingRequestNotFound(requestId));
        }

        // At this point, the request can be removed from the cache.
        this.sharingRequestCache.removeSharingRequest(requestId);

        WaypointSharingRequest request = optional.get();
        Waypoint waypoint = request.waypoint();
        WaypointUser target = request.target();

        // Checking that the waypoint is not already shared with the target.
        boolean isShared = this.waypointDAO.isShared(waypoint.getId(), target.getId());

        if (isShared) {
            return BusinessResult.error(new WaypointAlreadyShared(waypoint, target));
        }

        // Sharing the waypoint with the target.
        WaypointShare share = this.waypointDAO.shareWaypoint(waypoint.getId(), target.getId());

        return BusinessResult.success(share);
    }

    @Override
    public BusinessResult<WaypointSharingRequest, BusinessFailure> cancelWaypointSharingRequest(UUID requestId) {
        Validate.notNull(requestId, "requestId cannot be null");

        // Retrieving the sharing request.
        Optional<WaypointSharingRequest> optional = this.sharingRequestCache.getSharingRequest(requestId);

        if (optional.isEmpty()) {
            return BusinessResult.error(new SharingRequestNotFound(requestId));
        }

        // Removing the request from the cache.
        this.sharingRequestCache.removeSharingRequest(requestId);

        return BusinessResult.success(optional.get());
    }

    private boolean isValidWaypointName(String waypointName) {
        return waypointName != null && !waypointName.isEmpty() && waypointName.length() <= 32;
    }
}
