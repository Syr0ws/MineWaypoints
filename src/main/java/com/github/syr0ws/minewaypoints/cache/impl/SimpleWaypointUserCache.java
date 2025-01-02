package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.model.WaypointUserModel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWaypointUserCache implements WaypointUserCache<WaypointUserModel> {

    private final Map<UUID, WaypointUserModel> users = new ConcurrentHashMap<>();

    @Override
    public void addUser(WaypointUserModel user) {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        this.users.put(user.getId(), user);
    }

    @Override
    public void removeUser(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        this.users.remove(userId);
    }

    @Override
    public boolean hasData(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return this.users.containsKey(userId);
    }

    @Override
    public Optional<WaypointUserModel> getUser(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return Optional.ofNullable(this.users.get(userId));
    }

    @Override
    public Map<UUID, WaypointUserModel> getUsers() {
        return Collections.unmodifiableMap(this.users);
    }
}
