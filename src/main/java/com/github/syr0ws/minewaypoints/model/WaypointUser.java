package com.github.syr0ws.minewaypoints.model;

import java.util.*;

public class WaypointUser {

    private final UUID uuid;
    private final String name;

    private final Set<Long> waypoints = new HashSet<>();
    private final Set<WaypointShare> sharedWaypoints = new HashSet<>();

    public WaypointUser(UUID uuid, String name) {

        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        this.uuid = uuid;
        this.name = name;
    }

    public WaypointUser(UUID uuid, String name, Set<Long> waypoints, Set<WaypointShare> sharedWaypoints) {
        this(uuid, name);

        if(waypoints == null) {
            throw new IllegalArgumentException("waypoints cannot be null");
        }

        if(sharedWaypoints == null) {
            throw new IllegalArgumentException("sharedWaypoints cannot be null");
        }

        this.waypoints.addAll(waypoints);
        this.sharedWaypoints.addAll(sharedWaypoints);
    }

    public UUID getId() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void addWaypoint(Waypoint waypoint) {

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        this.waypoints.add(waypoint.getId());
    }

    public void removeWaypoint(long waypointId) {
        this.waypoints.remove(waypointId);
    }

    public boolean hasWaypoint(long waypointId) {
        return this.waypoints.contains(waypointId);
    }

    public Set<Long> getWaypoints() {
        return Collections.unmodifiableSet(this.waypoints);
    }

    public void shareWaypoint(WaypointShare share) {

        if(share == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        this.sharedWaypoints.add(share);
    }

    public void unshareWaypoint(long waypointId) {
        this.sharedWaypoints.removeIf(share -> share.getWaypointId() == waypointId);
    }

    public boolean hasSharedWaypoint(long waypointId) {
        return this.sharedWaypoints.stream().anyMatch(share -> share.getWaypointId() == waypointId);
    }

    public Set<WaypointShare> getSharedWaypoints() {
        return Collections.unmodifiableSet(this.sharedWaypoints);
    }
}
