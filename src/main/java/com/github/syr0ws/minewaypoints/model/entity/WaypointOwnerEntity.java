package com.github.syr0ws.minewaypoints.model.entity;

import com.github.syr0ws.minewaypoints.model.WaypointOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WaypointOwnerEntity extends WaypointUserEntity implements WaypointOwner {

    private final List<WaypointEntity> waypoints = new ArrayList<>();

    public WaypointOwnerEntity(UUID uuid, String name, List<WaypointEntity> waypoints) {
        super(uuid, name);

        if (waypoints == null) {
            throw new IllegalArgumentException("waypoints cannot be null");
        }

        this.waypoints.addAll(waypoints);
    }

    public void addWaypoint(WaypointEntity waypoint) {

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
    public Optional<WaypointEntity> getWaypointByName(String name) {
        return this.waypoints.stream()
                .filter(waypoint -> waypoint.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public Optional<WaypointEntity> getWaypointById(long waypointId) {
        return this.waypoints.stream()
                .filter(waypoint -> waypoint.getId() == waypointId)
                .findFirst();
    }

    @Override
    public List<WaypointEntity> getWaypoints() {
        return this.waypoints;
    }
}
