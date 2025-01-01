package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.model.WaypointUserEntity;

import java.util.*;

public class SimpleWaypointUserCache implements WaypointUserCache<WaypointUserEntity> {

    private final Map<UUID, WaypointUserEntity> users = new HashMap<>();

    @Override
    public void addUser(WaypointUserEntity user) {

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
    public Optional<WaypointUserEntity> getUser(UUID userId) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        return Optional.ofNullable(this.users.get(userId));
    }

    @Override
    public Map<UUID, WaypointUserEntity> getUsers() {
        return Collections.unmodifiableMap(this.users);
    }
}
