package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.Waypoint;

import java.util.List;
import java.util.Optional;

public interface WaypointCache<T extends Waypoint> {

    void addWaypoint(T waypoint);

    void addWaypoints(List<T> waypoints);

    void removeWaypoint(long waypointId);

    boolean hasWaypoint(long waypointId);

    Optional<T> getWaypoint(long waypointId);
}
