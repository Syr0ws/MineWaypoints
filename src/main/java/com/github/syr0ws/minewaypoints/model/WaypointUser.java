package com.github.syr0ws.minewaypoints.model;

import java.util.*;

public class WaypointUser {

    private final UUID uuid;
    private final String name;
    private final List<Waypoint> waypoints = new ArrayList<>();
    private final List<WaypointShare> shared = new ArrayList<>();
    private final List<Long> activated = new ArrayList<>();

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

    public WaypointUser(UUID uuid, String name, List<Waypoint> waypoints, List<WaypointShare> shared, List<Long> activated) {
        this(uuid, name);

        if (waypoints == null) {
            throw new IllegalArgumentException("waypoints cannot be null");
        }

        if (shared == null) {
            throw new IllegalArgumentException("shared cannot be null");
        }

        if (activated == null) {
            throw new IllegalArgumentException("activated cannot be null");
        }

        this.waypoints.addAll(waypoints);
        this.shared.addAll(shared);
        this.activated.addAll(activated);
    }

    public UUID getUUID() {
        return this.uuid;
    }

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

    public boolean removeWaypoint(long waypointId) {
        return this.waypoints.removeIf(waypoint -> waypoint.getId() == waypointId);
    }

    public boolean hasWaypoint(long waypointId) {
        return this.waypoints.stream().anyMatch(waypoint -> waypoint.getId() == waypointId);
    }

    public List<Waypoint> getWaypoints() {
        return Collections.unmodifiableList(this.waypoints);
    }

    public void share(Waypoint waypoint) {

        if (waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        if (!this.hasSharedWaypoint(waypoint.getId())) {
            this.shared.add(new WaypointShare(waypoint, new Date()));
        }
    }

    public boolean unshare(long waypointId) {
        return this.shared.removeIf(waypointShare -> waypointShare.getWaypoint().getId() == waypointId);
    }

    public boolean hasSharedWaypoint(long waypointId) {
        return this.shared.stream()
                .anyMatch(waypointShare -> waypointShare.getWaypoint().getId() == waypointId);
    }

    public List<WaypointShare> getShared() {
        return Collections.unmodifiableList(this.shared);
    }

    public void activate(long waypointId) {

        if (!this.canActivate(waypointId)) {
            throw new IllegalArgumentException("waypoint cannot be activated because it is not accessible to the user");
        }

        this.activated.add(waypointId);
    }

    public boolean deactivate(long waypointId) {
        return this.activated.remove(waypointId);
    }

    public boolean isActivated(long waypointId) {
        return this.activated.contains(waypointId);
    }

    public boolean canActivate(long waypointId) {
        return this.hasWaypoint(waypointId) || this.hasSharedWaypoint(waypointId);
    }

    public List<Waypoint> getActivated() {
        // A waypoint can be activated only if it belongs to the waypoints list or the shared list.
        // So, the following code tries to find the Waypoint by id from one of these two lists.
        return this.activated.stream()
                .map(waypointId -> this.waypoints.stream()
                        .filter(waypoint -> waypoint.getId() == waypointId)
                        .findFirst()
                        .orElse(this.shared.stream()
                                .map(WaypointShare::getWaypoint)
                                .filter(waypoint -> waypoint.getId() == waypointId)
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Waypoint inconsistency"))))
                .toList();
    }
}
