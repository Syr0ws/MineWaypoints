package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.Waypoint;

import java.util.Optional;

public interface WaypointCache<T extends Waypoint> {

    Optional<T> getWaypoint(long waypointId);
}
