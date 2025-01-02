package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.minewaypoints.cache.WaypointCache;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.model.WaypointModel;
import com.github.syr0ws.minewaypoints.model.WaypointShareModel;
import com.github.syr0ws.minewaypoints.model.WaypointUserModel;

import java.util.Optional;
import java.util.stream.Stream;

public class SimpleWaypointCache implements WaypointCache<WaypointModel> {

    private final WaypointUserCache<WaypointUserModel> waypointUserCache;

    public SimpleWaypointCache(WaypointUserCache<WaypointUserModel> waypointUserCache) {
        this.waypointUserCache = waypointUserCache;
    }

    @Override
    public Optional<WaypointModel> getWaypoint(long waypointId) {
        return this.waypointUserCache.getUsers().stream()
                .flatMap(user -> Stream.concat(
                        user.getWaypoints().stream(),
                        user.getSharedWaypoints().stream().map(WaypointShareModel::getWaypoint)))
                .filter(waypoint -> waypoint.getId() == waypointId)
                .findFirst();
    }
}
