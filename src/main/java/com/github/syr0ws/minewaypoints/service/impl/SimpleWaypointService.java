package com.github.syr0ws.minewaypoints.service.impl;

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
import java.util.function.Consumer;

public class SimpleWaypointService implements WaypointService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;
    private final WaypointUserCache<WaypointUserModel> cache;

    public SimpleWaypointService(Plugin plugin, WaypointDAO waypointDAO, WaypointUserCache<WaypointUserModel> cache) {

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
                throw new WaypointDataException("The user already has a waypoint with the same name");
            }

            // Creating the waypoint in the database.
            WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
            Waypoint waypoint = this.waypointDAO.createWaypoint(waypointUser, name, newIcon, waypointLocation);

            // Updating cache.
            waypointUser.addWaypoint(waypoint);

            resolve.accept(waypoint);
        }));
    }

    @Override
    public Promise<Void> updateWaypoint(Waypoint waypoint) {

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Updating database.
            this.waypointDAO.updateWaypoint(waypoint);

            // Updating cache.
            Consumer<Waypoint> waypointUpdater = waypointToUpdate -> {
                waypointToUpdate.setName(waypoint.getName());
                waypointToUpdate.setIcon(waypoint.getIcon());
                waypointToUpdate.setLocation(waypoint.getLocation());
            };

            this.cache.getUsers().values().forEach(user -> {

                // Update user's waypoints.
                user.getWaypoints().stream()
                        .filter(userWaypoint -> userWaypoint.getId() == waypoint.getId())
                        .forEach(waypointUpdater);

                // Update user's shared waypoints.
                user.getSharedWaypoints().stream()
                        .filter(share -> share.getWaypoint().getId() == waypoint.getId())
                        .forEach(share -> waypointUpdater.accept(share.getWaypoint()));
            });

            resolve.accept(null);
        });
    }

    @Override
    public Promise<Void> deleteWaypoint(long waypointId) {
        return new Promise<>((resolve, reject) -> {

            // Updating database.
            this.waypointDAO.deleteWaypoint(waypointId);

            // Updating cache.
            this.cache.getUsers().values().forEach(user -> {
                user.removeWaypoint(waypointId);
                user.unshareWaypoint(waypointId);
            });

            resolve.accept(null);
        });
    }

    @Override
    public Promise<WaypointShare> shareWaypoint(WaypointUser user, long waypointId) {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Updating database.
            WaypointShare share = this.waypointDAO.shareWaypoint(user, waypointId);

            // Updating cache.
            user.shareWaypoint(share);

            resolve.accept(share);
        });
    }

    @Override
    public Promise<Void> unshareWaypoint(WaypointUser user, long waypointId) {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Updating database.
            this.waypointDAO.unshareWaypoint(user, waypointId);

            // Updating cache.
            user.unshareWaypoint(waypointId);

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
