package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.minewaypoints.cache.WaypointCache;
import com.github.syr0ws.minewaypoints.model.WaypointModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWaypointCache implements WaypointCache<WaypointModel> {

    private final Map<Long, WaypointModel> waypoints = new ConcurrentHashMap<>();

    @Override
    public void addWaypoint(WaypointModel waypoint) {

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        // Store waypoint in cache if it has not been loaded before.
        if(!waypoints.containsKey(waypoint.getId())) {
            this.waypoints.put(waypoint.getId(), waypoint);
        }
    }

    @Override
    public void addWaypoints(List<WaypointModel> waypoints) {

        if(waypoints == null) {
            throw new IllegalArgumentException("waypoints cannot be null");
        }

        waypoints.forEach(this::addWaypoint);
    }

    @Override
    public void removeWaypoint(long waypointId) {
        this.waypoints.remove(waypointId);
    }

    @Override
    public boolean hasWaypoint(long waypointId) {
        return this.waypoints.containsKey(waypointId);
    }

    @Override
    public Optional<WaypointModel> getWaypoint(long waypointId) {
        return Optional.ofNullable(this.waypoints.get(waypointId));
    }
}
