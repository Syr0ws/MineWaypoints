package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.util.Async;
import com.github.syr0ws.minewaypoints.util.Callback;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class SimpleWaypointUserService implements WaypointUserService {

    private final Plugin plugin;
    private final WaypointUserDAO waypointUserDAO;
    private final Map<UUID, WaypointUser> cache = new HashMap<>();

    public SimpleWaypointUserService(Plugin plugin, WaypointUserDAO waypointUserDAO) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(waypointUserDAO == null) {
            throw new IllegalArgumentException("waypointUserDAO cannot be null");
        }

        this.plugin = plugin;
        this.waypointUserDAO = waypointUserDAO;
    }

    @Override
    public WaypointUser createData(UUID userId, String name) throws WaypointDataException {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        if(name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        // Creating user data.
        WaypointUser user = this.waypointUserDAO.createUser(userId, name);

        // Storing data in cache.
        this.cache.put(userId, user);

        return user;
    }

    @Override
    public void createDataAsync(UUID userId, String name, Callback<WaypointUser> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                WaypointUser user = this.createData(userId, name);
                callback.onSuccess(user);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public WaypointUser loadData(UUID userId) throws WaypointDataException {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        // Loading user data.
        WaypointUser user = this.waypointUserDAO.findUser(userId);

        // Storing data in cache.
        this.cache.put(userId, user);

        return user;
    }

    @Override
    public void loadDataAsync(UUID userId, Callback<WaypointUser> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                WaypointUser user = this.loadData(userId);
                callback.onSuccess(user);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void unloadData(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        this.cache.remove(userId);
    }

    @Override
    public boolean hasData(UUID userId) throws WaypointDataException {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return this.waypointUserDAO.userExists(userId);
    }

    @Override
    public boolean hasLoadedData(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return this.cache.containsKey(userId);
    }

    @Override
    public void hasDataAsync(UUID userId, Callback<Boolean> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                boolean exists = this.hasData(userId);
                callback.onSuccess(exists);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public WaypointUser getWaypointUser(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        WaypointUser user = this.cache.getOrDefault(userId, null);

        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return user;
    }

    @Override
    public List<WaypointUser> getWaypointUsers() {
        return new ArrayList<>(this.cache.values());
    }
}
