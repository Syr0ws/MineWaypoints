package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;

import java.util.UUID;

public class SimpleWaypointUserService implements WaypointUserService {

    private final WaypointUserDAO waypointUserDAO;
    private final WaypointUserCache<WaypointOwnerEntity> userCache;

    public SimpleWaypointUserService(WaypointUserDAO waypointUserDAO, WaypointUserCache<WaypointOwnerEntity> userCache) {
        Validate.notNull(waypointUserDAO, "waypointUserDAO cannot be null");
        Validate.notNull(userCache, "userCache cannot be null");

        this.waypointUserDAO = waypointUserDAO;
        this.userCache = userCache;
    }

    @Override
    public Promise<WaypointOwner> createData(UUID userId, String name) {
        Validate.notNull(userId, "userId cannot be null");
        Validate.notEmpty(name, "name cannot be null or empty");

        return new Promise<>((resolve, reject) -> {

            // Creating user data.
            WaypointOwnerEntity user = this.waypointUserDAO.createUser(userId, name);

            // Storing data in cache.
            this.userCache.addUser(user);

            resolve.accept(user);
        });
    }

    @Override
    public Promise<WaypointOwner> loadData(UUID userId) {
        Validate.notNull(userId, "userId cannot be null");

        return new Promise<>((resolve, reject) -> {

            // Loading user data.
            WaypointOwnerEntity user = this.waypointUserDAO.findOwner(userId)
                    .orElseThrow(() -> new NullPointerException(String.format("No user found with id %s", userId)));

            // Storing data in cache.
            this.userCache.addUser(user);

            resolve.accept(user);
        });
    }

    @Override
    public Promise<Void> unloadData(UUID userId) {
        Validate.notNull(userId, "userId cannot be null");

        return new Promise<>((resolve, reject) -> {

            // Removing user from cache.
            this.userCache.removeUser(userId);

            resolve.accept(null);
        });
    }

    @Override
    public Promise<Boolean> hasData(UUID userId) {
        Validate.notNull(userId, "userId cannot be null");

        return new Promise<>((resolve, reject) -> {
            boolean exists = this.waypointUserDAO.userExists(userId);
            resolve.accept(exists);
        });
    }
}
