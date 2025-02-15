package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWaypointUserCache implements WaypointUserCache<WaypointOwnerEntity> {

    private final Map<UUID, WaypointOwnerEntity> users = new ConcurrentHashMap<>();

    @Override
    public void addUser(WaypointOwnerEntity user) {
        Validate.notNull(user, "user cannot be null");

        this.users.put(user.getId(), user);
    }

    @Override
    public void removeUser(UUID userId) {
        Validate.notNull(userId, "userId cannot be null");

        this.users.remove(userId);
    }

    @Override
    public boolean hasData(UUID userId) {
        Validate.notNull(userId, "userId cannot be null");

        return this.users.containsKey(userId);
    }

    @Override
    public Optional<WaypointOwnerEntity> getUser(UUID userId) {
        Validate.notNull(userId, "userId cannot be null");

        return Optional.ofNullable(this.users.get(userId));
    }

    @Override
    public List<WaypointOwnerEntity> getUsers() {
        return new ArrayList<>(this.users.values());
    }
}
