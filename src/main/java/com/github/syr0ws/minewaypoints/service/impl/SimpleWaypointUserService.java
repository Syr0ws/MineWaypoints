package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.util.Promise;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class SimpleWaypointUserService implements WaypointUserService {

    private final WaypointUserDAO waypointUserDAO;
    private final Map<UUID, WaypointUser> cache = new HashMap<>();

    public SimpleWaypointUserService(WaypointUserDAO waypointUserDAO) {

        if(waypointUserDAO == null) {
            throw new IllegalArgumentException("waypointUserDAO cannot be null");
        }

        this.waypointUserDAO = waypointUserDAO;
    }

    @Override
    public Promise<WaypointUser> createData(UUID userId, String name) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        if(name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Creating user data.
            WaypointUser user = this.waypointUserDAO.createUser(userId, name);

            // Storing data in cache.
            this.cache.put(userId, user);

            resolve.accept(user);
        });
    }

    @Override
    public Promise<WaypointUser> loadData(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Loading user data.
            WaypointUser user = this.waypointUserDAO.findUser(userId);

            // Storing data in cache.
            this.cache.put(userId, user);

            resolve.accept(user);
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
    public Promise<Boolean> hasData(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new Promise<>((resolve, reject) -> {
            boolean exists = this.waypointUserDAO.userExists(userId);
            resolve.accept(exists);
        });
    }

    @Override
    public boolean hasLoadedData(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return this.cache.containsKey(userId);
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
