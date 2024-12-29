package com.github.syr0ws.minewaypoints.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WaypointUser {

    private final UUID uuid;
    private final String name;

    private final List<Waypoint> waypoints = new ArrayList<>();
    private final List<WaypointShare> sharedWaypoints = new ArrayList<>();

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

    public WaypointUser(UUID uuid, String name, List<Waypoint> waypoints, List<WaypointShare> sharedWaypoints) {
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

        if(!this.hasWaypoint(waypoint.getId())) {
            this.waypoints.add(waypoint);
        }
    }

    public void removeWaypoint(long waypointId) {
        this.waypoints.removeIf(waypoint -> waypoint.getId() == waypointId);
    }

    public boolean hasWaypoint(long waypointId) {
        return this.waypoints.stream()
                .anyMatch(waypoint -> waypoint.getId() == waypointId);
    }

    public boolean hasWaypointByName(String name) {

        if(name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        return this.waypoints.stream()
                .anyMatch(waypoint -> waypoint.getName().equalsIgnoreCase(name));
    }

    public List<Waypoint> getWaypoints() {
        return Collections.unmodifiableList(this.waypoints);
    }

    public void shareWaypoint(WaypointShare share) {

        if(share == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        this.sharedWaypoints.add(share);
    }

    public void unshareWaypoint(long waypointId) {
        this.sharedWaypoints
                .removeIf(share -> share.getWaypoint().getId() == waypointId);
    }

    public boolean hasSharedWaypoint(long waypointId) {
        return this.sharedWaypoints.stream()
                .anyMatch(share -> share.getWaypoint().getId() == waypointId);
    }

    public List<WaypointShare> getSharedWaypoints() {
        return Collections.unmodifiableList(this.sharedWaypoints);
    }
}
