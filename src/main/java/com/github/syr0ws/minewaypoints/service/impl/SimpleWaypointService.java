package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.crafter.config.ConfigUtil;
import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.event.WaypointDeleteEvent;
import com.github.syr0ws.minewaypoints.event.WaypointUnshareEvent;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointShareEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.service.util.WaypointEnums;
import com.github.syr0ws.minewaypoints.util.WaypointValidate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleWaypointService implements WaypointService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;
    private final WaypointUserDAO waypointUserDAO;
    private final WaypointUserCache<WaypointOwnerEntity> userCache;

    public SimpleWaypointService(Plugin plugin, WaypointDAO waypointDAO, WaypointUserDAO waypointUserDAO, WaypointUserCache<WaypointOwnerEntity> userCache) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointDAO, "waypointDAO cannot be null");
        Validate.notNull(waypointUserDAO, "waypointUserDAO cannot be null");
        Validate.notNull(userCache, "userCache cannot be null");

        this.plugin = plugin;
        this.waypointDAO = waypointDAO;
        this.waypointUserDAO = waypointUserDAO;
        this.userCache = userCache;
    }

    @Override
    public Promise<Waypoint> createWaypoint(UUID ownerId, String name, Material icon, Location location) {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notNull(location, "location cannot be null");
        WaypointValidate.checkWaypointName(name);

        return new Promise<>(((resolve, reject) -> {

            Material newIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            WaypointOwnerEntity owner = this.userCache.getUser(ownerId)
                    .orElseThrow(() -> new NullPointerException(String.format("User %s not found", ownerId)));

            // Checking that the user does not have a waypoint with the same name.
            if (owner.hasWaypointByName(name)) {
                throw new WaypointDataException(String.format("User '%s' already has a waypoint with the name '%s'", ownerId, name));
            }

            // Creating the waypoint in the database.
            WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
            WaypointEntity waypoint = this.waypointDAO.createWaypoint(owner, name, newIcon, waypointLocation);

            // Updating cache.
            owner.addWaypoint(waypoint);

            resolve.accept(waypoint);
        }));
    }

    @Override
    public Promise<Void> updateWaypointIcon(long waypointId, Material icon) {
        return new Promise<>((resolve, reject) -> {

            // Retrieving the waypoint.
            WaypointEntity waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException(String.format("No waypoint found with id %d", waypointId)));

            // Updating the icon of the waypoint.
            Material newIcon = icon == null ? this.getDefaultWaypointIcon() : icon;
            waypoint.setIcon(newIcon);

            this.waypointDAO.updateWaypoint(waypoint);

            // Updating the cache.
            this.userCache.getUser(waypoint.getOwner().getId())
                    .flatMap(owner -> owner.getWaypointById(waypointId))
                    .ifPresent(cached -> cached.setIcon(newIcon));

            resolve.accept(null);
        });
    }

    @Override
    public Promise<Void> updateWaypointName(long waypointId, String newName) {
        WaypointValidate.checkWaypointName(newName);

        return new Promise<>(((resolve, reject) -> {

            // Retrieving the waypoint.
            WaypointEntity waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException(String.format("No waypoint found with id %d", waypointId)));

            UUID ownerId = waypoint.getOwner().getId();

            // Checking that the owner does not have a waypoint with the same name.
            boolean hasWaypointByName = this.waypointDAO.hasWaypointByName(ownerId, newName);

            if (hasWaypointByName) {
                throw new IllegalStateException(String.format("User %s already has a waypoint with the name '%s'", ownerId, newName));
            }

            // Updating the name of the waypoint.
            waypoint.setName(newName);
            this.waypointDAO.updateWaypoint(waypoint);

            // Updating the cache.
            this.userCache.getUser(ownerId)
                    .flatMap(owner -> owner.getWaypointById(waypointId))
                    .ifPresent(cached -> cached.setName(newName));

            resolve.accept(null);
        }));
    }

    @Override
    public Promise<Void> updateWaypointLocation(long waypointId, Location location) {
        Validate.notNull(location, "location cannot be null");

        return new Promise<>((resolve, reject) -> {

            // Retrieving the waypoint.
            WaypointEntity waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException(String.format("No waypoint found with id %d", waypointId)));

            // Updating the icon of the waypoint.
            WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
            waypoint.setLocation(waypointLocation);

            this.waypointDAO.updateWaypoint(waypoint);

            // Updating the cache.
            this.userCache.getUser(waypoint.getOwner().getId())
                    .flatMap(owner -> owner.getWaypointById(waypointId))
                    .ifPresent(cached -> cached.setLocation(waypointLocation));

            resolve.accept(null);
        });
    }

    @Override
    public Promise<Boolean> deleteWaypoint(long waypointId) {
        return new Promise<>((resolve, reject) -> {

            // Retrieving the waypoint if it exists.
            Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

            if (optional.isEmpty()) {
                resolve.accept(false);
                return;
            }

            WaypointEntity waypoint = optional.get();

            // Calling event.
            Set<WaypointUser> waypointSharedWith = this.waypointDAO.findSharedWith(waypoint).stream()
                    .map(WaypointShareEntity::getSharedWith)
                    .collect(Collectors.toSet());

            // Deleting the waypoint.
            // This should delete cascade everything that depends on the waypoint.
            this.waypointDAO.deleteWaypoint(waypointId);

            // Updating cache.
            this.userCache.getUser(waypoint.getOwner().getId())
                    .ifPresent(owner -> owner.removeWaypoint(waypointId));

            resolve.accept(true);

            // Event must be called synchronously.
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                WaypointDeleteEvent event = new WaypointDeleteEvent(waypoint, waypointSharedWith);
                Bukkit.getPluginManager().callEvent(event);
            });
        });
    }

    @Override
    public Promise<WaypointEnums.WaypointShareStatus> shareWaypoint(String targetUserName, long waypointId) {
        Validate.notNull(targetUserName, "targetUserName cannot be null");

        return new Promise<>((resolve, reject) -> {

            WaypointUserEntity toUser = this.waypointUserDAO.findUserByName(targetUserName)
                    .orElseThrow(() -> new NullPointerException(String.format("No user found with name %s", targetUserName)));

            Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

            if(optional.isEmpty()) {
                resolve.accept(WaypointEnums.WaypointShareStatus.WAYPOINT_NOT_FOUND);
                return;
            }

            boolean isShared = this.waypointDAO.isShared(targetUserName, waypointId);

            if(isShared) {
                resolve.accept(WaypointEnums.WaypointShareStatus.ALREADY_SHARED);
                return;
            }

            // Updating database.
            WaypointEntity waypoint = optional.get();
            this.waypointDAO.shareWaypoint(toUser, waypoint);

            resolve.accept(WaypointEnums.WaypointShareStatus.SHARED);

            // No cache update here, as data is always retrieved from the database to ensure consistency.
        });
    }

    @Override
    public Promise<Boolean> unshareWaypoint(String targetName, long waypointId) {
        Validate.notNull(targetName, "targetName cannot be null");

        return new Promise<>((resolve, reject) -> {

            Optional<WaypointShare> optional = this.waypointDAO.findWaypointShare(targetName, waypointId);

            if (optional.isEmpty()) {
                resolve.accept(false);
                return;
            }

            WaypointShare share = optional.get();

            // Unsharing the waypoint.
            boolean unshared = this.waypointDAO.unshareWaypoint(targetName, waypointId);
            resolve.accept(unshared);

            // Note: No cache update here, as data is always retrieved from the database to ensure consistency.
            // The database should also take care of removing any entry in the activated_waypoints table.

            // Event must be called synchronously.
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                WaypointUnshareEvent event = new WaypointUnshareEvent(share.getWaypoint(), share.getSharedWith());
                Bukkit.getPluginManager().callEvent(event);
            });
        });
    }

    @Override
    public Promise<Boolean> isWaypointSharedWith(String targetName, long waypointId) {
        Validate.notNull(targetName, "targetName cannot be null");

        return new Promise<>((resolve, reject) -> {
            boolean isShared = this.waypointDAO.isShared(targetName, waypointId);
            resolve.accept(isShared);
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWaypoints(UUID userId) {
        Validate.notNull(userId, "userId cannot be null");

        return new Promise<>((resolve, reject) -> {

            List<WaypointShare> sharedWaypoints = this.waypointDAO.findSharedWaypoints(userId).stream()
                    .map(waypointShareEntity -> (WaypointShare) waypointShareEntity)
                    // Using this, the list is still mutable.
                    .collect(Collectors.toCollection(ArrayList::new));

            resolve.accept(sharedWaypoints);
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWith(long waypointId) {
        return new Promise<>((resolve, reject) -> {

            WaypointEntity waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException(String.format("No waypoint found with id %d", waypointId)));

            List<WaypointShare> sharedWith = this.waypointDAO.findSharedWith(waypoint).stream()
                    .map(waypointShareEntity -> (WaypointShare) waypointShareEntity)
                    // Using this, the list is still mutable.
                    .collect(Collectors.toCollection(ArrayList::new));

            resolve.accept(sharedWith);
        });
    }

    private Material getDefaultWaypointIcon() throws WaypointDataException {
        try {
            return ConfigUtil.getMaterial(this.plugin.getConfig(), "default-waypoint-icon");
        } catch (ConfigurationException exception) {
            throw new WaypointDataException("Cannot assign waypoint icon", exception);
        }
    }
}
