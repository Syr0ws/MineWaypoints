package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.exception.ConfigurationException;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.util.Async;
import com.github.syr0ws.minewaypoints.util.Callback;
import com.github.syr0ws.minewaypoints.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SimpleWaypointService implements WaypointService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;
    private final WaypointUserService waypointUserService;

    public SimpleWaypointService(Plugin plugin, WaypointDAO waypointDAO, WaypointUserService waypointUserService) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(waypointDAO == null) {
            throw new IllegalArgumentException("waypointDAO cannot be null");
        }

        if(waypointUserService == null) {
            throw new IllegalArgumentException("waypointUserService cannot be null");
        }

        this.plugin = plugin;
        this.waypointDAO = waypointDAO;
        this.waypointUserService = waypointUserService;
    }

    @Override
    public Waypoint createWaypoint(UUID ownerId, String name, Material icon, Location location) throws WaypointDataException {

        if(ownerId == null) {
            throw new IllegalArgumentException("ownerId cannot be null");
        }

        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null");
        }

        if(location == null) {
            throw new IllegalArgumentException("location cannot be null");
        }

        if(icon == null) {
            icon = this.getDefaultWaypointIcon();
        }

        WaypointUser waypointUser = this.waypointUserService.getWaypointUser(ownerId);

        // Checking that the user does not have a waypoint with the same name.
        if(waypointUser.hasWaypointByName(name)) {
            throw new WaypointDataException("The user already has a waypoint with the same name");
        }

        // Creating the waypoint in the database.
        WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);
        Waypoint waypoint = this.waypointDAO.createWaypoint(waypointUser, name, icon, waypointLocation);

        // Updating cache.
        waypointUser.addWaypoint(waypoint);

        return waypoint;
    }

    @Override
    public void createWaypointAsync(UUID ownerId, String name, Material icon, Location location, Callback<Waypoint> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                Waypoint waypoint = this.createWaypoint(ownerId, name, icon, location);
                callback.onSuccess(waypoint);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void updateWaypoint(Waypoint waypoint) throws WaypointDataException {

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        // Updating database.
        this.waypointDAO.updateWaypoint(waypoint);

        // TODO: Update cache.
    }

    @Override
    public void updateWaypointAsync(Waypoint waypoint, Callback<Waypoint> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                this.updateWaypoint(waypoint);
                callback.onSuccess(waypoint);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void deleteWaypoint(long waypointId) throws WaypointDataException {

        // Updating database.
        this.waypointDAO.deleteWaypoint(waypointId);

        // Updating cache.
        this.waypointUserService.getWaypointUsers().forEach(user -> {
            user.removeWaypoint(waypointId);
            user.unshareWaypoint(waypointId);
        });
    }

    @Override
    public void deleteWaypointAsync(long waypointId, Callback<Void> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                this.deleteWaypoint(waypointId);
                callback.onSuccess(null);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public WaypointShare shareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        // Updating database.
        WaypointShare share = this.waypointDAO.shareWaypoint(user, waypointId);

        // Updating cache.
        user.shareWaypoint(share);

        return share;
    }

    @Override
    public void shareWaypointAsync(WaypointUser user, long waypointId, Callback<WaypointShare> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                WaypointShare share = this.shareWaypoint(user, waypointId);
                callback.onSuccess(share);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void unshareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        // Updating database.
        this.waypointDAO.unshareWaypoint(user, waypointId);

        // Updating cache.
        user.unshareWaypoint(waypointId);
    }

    @Override
    public void unshareWaypointAsync(WaypointUser user, long waypointId, Callback<Void> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                this.unshareWaypoint(user, waypointId);
                callback.onSuccess(null);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
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
