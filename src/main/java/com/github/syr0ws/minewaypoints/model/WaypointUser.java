package com.github.syr0ws.minewaypoints.model;

import java.util.*;

public interface WaypointUser {

    UUID getId();

    String getName();

    boolean hasWaypoint(long waypointId);

    boolean hasWaypointByName(String name);

    Optional<Waypoint> getWaypointByName(String name);

    Optional<Waypoint> getWaypointById(long waypointId);

    List<Waypoint> getWaypoints();

    List<WaypointShare> getSharedWaypoints();
}
