package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.cache.WaypointCache;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.exception.ConfigurationException;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.*;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.util.ConfigUtil;
import com.github.syr0ws.minewaypoints.util.Promise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SimpleWaypointService implements WaypointService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;
    private final WaypointUserCache<WaypointUserModel> cache;
    private final WaypointCache<WaypointModel> waypointCache;

    public SimpleWaypointService(Plugin plugin, WaypointDAO waypointDAO, WaypointUserCache<WaypointUserModel> cache, WaypointCache<WaypointModel> waypointCache) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(waypointDAO == null) {
            throw new IllegalArgumentException("waypointDAO cannot be null");
        }

        if(cache == null) {
            throw new IllegalArgumentException("cache cannot be null");
        }

        this.plugin = plugin;
        this.waypointDAO = waypointDAO;
        this.cache = cache;
        this.waypointCache = waypointCache;
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

            WaypointUserModel waypointUser = this.cache.getUser(ownerId)
                    .orElseThrow(() -> new NullPointerException("User not found"));

            // Checking that the user does not have a waypoint with the same name.
            if(waypointUser.hasWaypointByName(name)) {
                throw new WaypointDataException("User already has a waypoint with the same name");
            }

            // Creating the waypoint in the database.
            WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
            WaypointModel waypoint = this.waypointDAO.createWaypoint(waypointUser, name, newIcon, waypointLocation);

            // Updating cache.
            waypointUser.addWaypoint(waypoint);

            resolve.accept(waypoint);
        }));
    }

    @Override
    public Promise<Void> updateWaypointIcon(long waypointId, Material icon) {
        return new Promise<>((resolve, reject) -> {

            // Retrieving the waypoint.
            WaypointModel waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException("Waypoint not found"));

            // Updating the icon of the waypoint.
            Material newIcon = icon == null ? this.getDefaultWaypointIcon() : icon;
            waypoint.setIcon(newIcon);

            this.waypointDAO.updateWaypoint(waypoint);

            // Update the cache.
            this.waypointCache.getWaypoint(waypointId)
                    .ifPresent(cached -> cached.setIcon(icon));

            resolve.accept(null);
        });
    }

    @Override
    public Promise<Void> updateWaypointName(long waypointId, String newName) {

        if(newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("newName cannot be null or empty");
        }

        return new Promise<>(((resolve, reject) -> {

            // Retrieving the owner of the waypoint.
            WaypointUserModel owner = this.cache.getUsers().stream()
                    .filter(user -> user.hasWaypoint(waypointId))
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException("Waypoint owner not found"));

            WaypointModel waypoint = owner.getWaypointById(waypointId)
                    .orElseThrow(() -> new NullPointerException("Waypoint not found"));

            // Checking that the user does not have a waypoint with the same name.
            if(owner.hasWaypointByName(newName)) {
                throw new WaypointDataException("User already has a waypoint with the same name");
            }

            // Updating the name of the waypoint.
            waypoint.setName(newName);
            this.waypointDAO.updateWaypoint(waypoint);

            // Updating the cache.
            this.waypointCache.getWaypoint(waypointId)
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
            WaypointModel waypoint = this.waypointDAO.findWaypoint(waypointId)
                    .orElseThrow(() -> new NullPointerException("Waypoint not found"));

            // Updating the waypoint.
            WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
            waypoint.setLocation(waypointLocation);

            this.waypointDAO.updateWaypoint(waypoint);

            // Updating the cache.
            this.waypointCache.getWaypoint(waypointId)
                    .ifPresent(cached -> cached.setLocation(waypointLocation));

            resolve.accept(null);
        });
    }

    @Override
    public Promise<Void> deleteWaypoint(long waypointId) {
        return new Promise<>((resolve, reject) -> {

            WaypointModel waypoint = this.waypointCache.getWaypoint(waypointId)
                    .orElse(null);

            // Waypoint not found.
            if(waypoint == null) {
                resolve.accept(null);
                return;
            }

            // Updating database. This should delete cascade everything that depends on the waypoint.
            this.waypointDAO.deleteWaypoint(waypointId);

            // Updating cache.
            waypoint.getOwner().removeWaypoint(waypointId);

            this.cache.getUsers().forEach(user -> {
                user.unshareWaypoint(waypointId);
            });

            resolve.accept(null);
        });
    }

    @Override
    public Promise<WaypointShare> shareWaypoint(UUID userId, long waypointId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        // When shared, the waypoint should already be loaded in the cache.
        WaypointUserModel waypointUser = this.cache.getUser(userId)
                .orElseThrow(() -> new NullPointerException("User not found"));

        return new Promise<>((resolve, reject) -> {

            // Updating database.
            WaypointShareModel share = this.waypointDAO.shareWaypoint(waypointUser.getId(), waypointId);

            // Updating cache.
            waypointUser.shareWaypoint(new WaypointShareModel(
                    this.waypointCache.getWaypoint(waypointId).orElse(share.getWaypoint()),
                    share.getSharedAt()
            ));

            resolve.accept(share);
        });
    }

    @Override
    public Promise<Void> unshareWaypoint(UUID userId, long waypointId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        WaypointUserModel waypointUser = this.cache.getUser(userId)
                .orElseThrow(() -> new NullPointerException("User not found"));

        return new Promise<>((resolve, reject) -> {

            // Updating database.
            this.waypointDAO.unshareWaypoint(waypointUser.getId(), waypointId);

            // Updating cache.
            waypointUser.unshareWaypoint(waypointId);

            resolve.accept(null);
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
