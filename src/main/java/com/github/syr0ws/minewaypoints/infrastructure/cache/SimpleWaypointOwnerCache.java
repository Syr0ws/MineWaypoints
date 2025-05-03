package com.github.syr0ws.minewaypoints.infrastructure.cache;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.cache.WaypointOwnerCache;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointOwnerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWaypointOwnerCache implements WaypointOwnerCache<WaypointOwnerEntity> {

    private final Map<UUID, WaypointOwnerEntity> users = new ConcurrentHashMap<>();

    @Override
    public void addData(WaypointOwnerEntity owner) {
        Validate.notNull(owner, "user cannot be null");

        this.users.put(owner.getId(), owner);
    }

    @Override
    public void removeData(UUID ownerId) {
        Validate.notNull(ownerId, "userId cannot be null");

        this.users.remove(ownerId);
    }

    @Override
    public boolean hasData(UUID ownerId) {
        Validate.notNull(ownerId, "userId cannot be null");

        return this.users.containsKey(ownerId);
    }

    @Override
    public Optional<WaypointOwnerEntity> getOwner(UUID ownerId) {
        Validate.notNull(ownerId, "userId cannot be null");

        return Optional.ofNullable(this.users.get(ownerId));
    }

    @Override
    public List<WaypointOwnerEntity> getOwners() {
        return new ArrayList<>(this.users.values());
    }
}
