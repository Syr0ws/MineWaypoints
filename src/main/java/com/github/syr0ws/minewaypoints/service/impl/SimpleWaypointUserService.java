package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.cache.impl.SimpleWaypointUserCache;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.util.Promise;

import java.util.UUID;

public class SimpleWaypointUserService implements WaypointUserService {

    private final WaypointUserDAO waypointUserDAO;
    private final SimpleWaypointUserCache cache;

    public SimpleWaypointUserService(WaypointUserDAO waypointUserDAO, SimpleWaypointUserCache cache) {

        if(waypointUserDAO == null) {
            throw new IllegalArgumentException("waypointUserDAO cannot be null");
        }

        if(cache == null) {
            throw new IllegalArgumentException("cache cannot be null");
        }

        this.waypointUserDAO = waypointUserDAO;
        this.cache = cache;
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
            this.cache.addUser(user);

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
            this.cache.addUser(userId, user);

            resolve.accept(user);
        });
    }

    @Override
    public Promise<Void> unloadData(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new Promise<>((resolve, reject) -> {
            this.cache.removeUser(userId);
            resolve.accept(null);
        });
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
}
