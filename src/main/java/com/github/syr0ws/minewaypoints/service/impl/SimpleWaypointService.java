package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.crafter.config.ConfigUtil;
import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointShareEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.util.Promise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SimpleWaypointService implements WaypointService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;
    private final WaypointUserDAO waypointUserDAO;
    private final WaypointUserCache<WaypointOwnerEntity> userCache;

    public SimpleWaypointService(Plugin plugin, WaypointDAO waypointDAO, WaypointUserDAO waypointUserDAO, WaypointUserCache<WaypointOwnerEntity> userCache) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(waypointDAO == null) {
            throw new IllegalArgumentException("waypointDAO cannot be null");
        }

        if(waypointUserDAO == null) {
            throw new IllegalArgumentException("waypointUserDAO cannot be null");
        }

        if(userCache == null) {
            throw new IllegalArgumentException("userCache cannot be null");
        }

        this.plugin = plugin;
        this.waypointDAO = waypointDAO;
        this.waypointUserDAO = waypointUserDAO;
        this.userCache = userCache;
    }

    @Override
    public Promise<Waypoint> createWaypoint(UUID ownerId, String name, Material icon, Location location) {

        if(ownerId == null) {
            throw new IllegalArgumentException("ownerId cannot be null");
        }

        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }

        if(location == null) {
            throw new IllegalArgumentException("location cannot be null");
        }

        return new Promise<>(((resolve, reject) -> {

            Material newIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            WaypointOwnerEntity owner = this.userCache.getUser(ownerId)
                    .orElseThrow(() -> new NullPointerException(String.format("User %s not found", ownerId)));

            // Checking that the user does not have a waypoint with the same name.
            if(owner.hasWaypointByName(name)) {
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

        if(newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("newName cannot be null or empty");
        }

        return new Promise<>(((resolve, reject) -> {

            // Retrieving the waypoint.
            WaypointEntity waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException(String.format("No waypoint found with id %d", waypointId)));

            UUID ownerId = waypoint.getOwner().getId();

            // Checking that the owner does not have a waypoint with the same name.
            boolean hasWaypointByName = this.waypointDAO.hasWaypointByName(ownerId, newName);

            if(hasWaypointByName) {
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

        if(location == null) {
            throw new IllegalArgumentException("location cannot be null");
        }

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
    public Promise<Void> deleteWaypoint(long waypointId) {
        return new Promise<>((resolve, reject) -> {

            // Retrieving the waypoint if it exists.
            Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

            if(optional.isEmpty()) {
                resolve.accept(null);
                return;
            }

            WaypointEntity waypoint = optional.get();

            // Updating database. This should delete cascade everything that depends on the waypoint.
            this.waypointDAO.deleteWaypoint(waypointId);

            // Updating cache.
            this.userCache.getUser(waypoint.getOwner().getId())
                    .ifPresent(owner -> owner.removeWaypoint(waypointId));

            resolve.accept(null);
        });
    }

    @Override
    public Promise<WaypointShare> shareWaypoint(String targetUserName, long waypointId) {

        if(targetUserName == null) {
            throw new IllegalArgumentException("targetUserName cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            WaypointUserEntity toUser = this.waypointUserDAO.findUserByName(targetUserName)
                    .orElseThrow(() -> new NullPointerException(String.format("No user found with name %s", targetUserName)));

            WaypointEntity waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException(String.format("No waypoint found with id %d", waypointId)));

            // Updating database.
            WaypointShareEntity share = this.waypointDAO.shareWaypoint(toUser, waypoint);

            // No cache update here, as data is always retrieved from the database to ensure consistency.

            resolve.accept(share);
        });
    }

    @Override
    public Promise<Boolean> unshareWaypoint(String targetUserName, long waypointId) {

        if(targetUserName == null) {
            throw new IllegalArgumentException("targetUserName cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Updating database.
            boolean unshared = this.waypointDAO.unshareWaypoint(targetUserName, waypointId);

            // No cache update here, as data is always retrieved from the database to ensure consistency.

            resolve.accept(unshared);
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWaypoints(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            List<WaypointShare> sharedWaypoints = this.waypointDAO.findSharedWaypoints(userId).stream()
                    .map(waypointShareEntity -> (WaypointShare) waypointShareEntity)
                    .toList();

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
                    .toList();

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
