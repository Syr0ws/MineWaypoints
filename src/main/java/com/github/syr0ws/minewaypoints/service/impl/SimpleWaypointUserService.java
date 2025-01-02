package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.cache.WaypointCache;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.model.WaypointModel;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.WaypointUserModel;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.util.Promise;

import java.util.List;
import java.util.UUID;

public class SimpleWaypointUserService implements WaypointUserService {

    private final WaypointUserDAO waypointUserDAO;
    private final WaypointDAO waypointDAO;
    private final WaypointUserCache<WaypointUserModel> waypointUserCache;
    private final WaypointCache<WaypointModel> waypointCache;

    public SimpleWaypointUserService(WaypointUserDAO waypointUserDAO, WaypointDAO waypointDAO, WaypointUserCache<WaypointUserModel> waypointUserCache, WaypointCache<WaypointModel> waypointCache) {

        if (waypointUserDAO == null) {
            throw new IllegalArgumentException("waypointUserDAO cannot be null");
        }

        if (waypointDAO == null) {
            throw new IllegalArgumentException("waypointDAO cannot be null");
        }

        if (waypointUserCache == null) {
            throw new IllegalArgumentException("cache cannot be null");
        }

        if (waypointCache == null) {
            throw new IllegalArgumentException("waypointCache cannot be null");
        }

        this.waypointUserDAO = waypointUserDAO;
        this.waypointDAO = waypointDAO;
        this.waypointUserCache = waypointUserCache;
        this.waypointCache = waypointCache;
    }

    @Override
    public Promise<WaypointUser> createData(UUID userId, String name) {

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Creating user data.
            WaypointUserModel user = this.waypointUserDAO.createUser(userId, name);

            // Storing data in cache.
            this.waypointUserCache.addUser(user);

            resolve.accept(user);
        });
    }

    @Override
    public Promise<WaypointUser> loadData(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            // Loading user data.
            WaypointUserModel user = this.waypointUserDAO.findUser(userId);

            List<WaypointModel> waypoints = this.waypointDAO.findWaypoints(userId);
            List<WaypointModel> sharedWaypoints = this.waypointDAO.findSharedWaypoints(userId);

            // Storing data in cache.
            this.waypointUserCache.addUser(user);

            this.waypointCache.addWaypoints(waypoints);
            this.waypointCache.addWaypoints(sharedWaypoints);

            resolve.accept(user);
        });
    }

    @Override
    public Promise<Void> unloadData(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new Promise<>((resolve, reject) -> {

            WaypointUserModel user = this.waypointUserCache.getUser(userId)
                    .orElseThrow(() -> new NullPointerException("User not found"));

            // Removing waypoints that are not used by other users from the cache.
            this.removeUnusedWaypointsFromCache(user);

            // Removing user from cache.
            this.waypointUserCache.removeUser(userId);

            resolve.accept(null);
        });
    }

    @Override
    public Promise<Boolean> hasData(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return new Promise<>((resolve, reject) -> {
            boolean exists = this.waypointUserDAO.userExists(userId);
            resolve.accept(exists);
        });
    }

    private void removeUnusedWaypointsFromCache(WaypointUserModel user) {
        user.getWaypoints().stream()
                .filter(waypoint ->
                        this.waypointUserCache.getUsers().values().stream()
                                .filter(other -> !other.getId().equals(user.getId()))
                                .noneMatch(other -> other.hasWaypoint(waypoint.getId()) || user.hasSharedWaypoint(waypoint.getId()))
                ).forEach(waypoint -> this.waypointCache.removeWaypoint(waypoint.getId()));
    }
}
