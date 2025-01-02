package com.github.syr0ws.minewaypoints.model;

import java.util.*;

public class WaypointUserModel implements WaypointUser {

    private final UUID uuid;
    private final String name;

    private final List<Waypoint> waypoints = new ArrayList<>();
    private final List<WaypointShare> sharedWaypoints = new ArrayList<>();

    public WaypointUserModel(UUID uuid, String name) {

        if (uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        this.uuid = uuid;
        this.name = name;
    }

    public WaypointUserModel(UUID uuid, String name, List<Waypoint> waypoints, List<WaypointShare> sharedWaypoints) {
        this(uuid, name);

        if (waypoints == null) {
            throw new IllegalArgumentException("waypoints cannot be null");
        }

        if (sharedWaypoints == null) {
            throw new IllegalArgumentException("sharedWaypoints cannot be null");
        }

        this.waypoints.addAll(waypoints);
        this.sharedWaypoints.addAll(sharedWaypoints);
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void addWaypoint(Waypoint waypoint) {

        if (waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        if (!this.hasWaypoint(waypoint.getId())) {
            this.waypoints.add(waypoint);
        }
    }

    public void removeWaypoint(long waypointId) {
        this.waypoints.removeIf(waypoint -> waypoint.getId() == waypointId);
    }

    @Override
    public boolean hasWaypoint(long waypointId) {
        return this.waypoints.stream()
                .anyMatch(waypoint -> waypoint.getId() == waypointId);
    }

    @Override
    public boolean hasWaypointByName(String name) {

        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        return this.waypoints.stream()
                .anyMatch(waypoint -> waypoint.getName().equalsIgnoreCase(name));
    }

    @Override
    public Optional<Waypoint> getWaypointByName(String name) {
        return this.waypoints.stream()
                .filter(waypoint -> waypoint.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public Optional<Waypoint> getWaypointById(long waypointId) {
        return this.waypoints.stream()
                .filter(waypoint -> waypoint.getId() == waypointId)
                .findFirst();
    }

    @Override
    public List<Waypoint> getWaypoints() {
        return Collections.unmodifiableList(this.waypoints);
    }

    public void shareWaypoint(WaypointShare share) {

        if (share == null) {
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

    @Override
    public List<WaypointShare> getSharedWaypoints() {
        return Collections.unmodifiableList(this.sharedWaypoints);
    }
}
