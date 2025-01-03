package com.github.syr0ws.minewaypoints.model;

import java.util.*;

public interface WaypointUser {

    UUID getId();

    String getName();

    boolean hasWaypoint(long waypointId);

    boolean hasWaypointByName(String name);

    Optional<? extends Waypoint> getWaypointByName(String name);

    Optional<? extends Waypoint> getWaypointById(long waypointId);

    List<? extends Waypoint> getWaypoints();

    List<? extends WaypointShare> getSharedWaypoints();
}
