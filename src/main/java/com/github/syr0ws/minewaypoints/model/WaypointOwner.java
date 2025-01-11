package com.github.syr0ws.minewaypoints.model;

import java.util.List;
import java.util.Optional;

public interface WaypointOwner extends WaypointUser {

    boolean hasWaypoint(long waypointId);

    boolean hasWaypointByName(String name);

    Optional<? extends Waypoint> getWaypointByName(String name);

    Optional<? extends Waypoint> getWaypointById(long waypointId);

    List<? extends Waypoint> getWaypoints();
}
