package com.github.syr0ws.minewaypoints.model;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WaypointOwner {

    private final UUID uuid;
    private final String name;
    private final List<Waypoint> waypoints = new ArrayList<>();

    private Waypoint activated;

    public WaypointOwner(UUID uuid, String name, List<Waypoint> waypoints) {

        if(uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        if(name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        if(waypoints == null) {
            throw new IllegalArgumentException("waypoints cannot be null");
        }

        this.uuid = uuid;
        this.name = name;
        this.waypoints.addAll(waypoints);
    }

    public WaypointOwner(Player player) {
        this(player.getUniqueId(), player.getName(), Collections.emptyList());
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Waypoint getActivated() {
        return this.activated;
    }

    public void setActivated(long waypointId) {
        this.activated = this.waypoints.stream()
                .filter(waypoint -> waypoint.getId() == waypointId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No waypoint with id %d found", waypointId)));
    }

    public void addWaypoint(Waypoint waypoint) {

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        this.waypoints.add(waypoint);
    }

    public boolean removeWaypoint(long waypointId) {
        return this.waypoints.removeIf(waypoint -> waypoint.getId() ==  waypointId);
    }

    public boolean hasWaypoint(long waypointId) {
        return this.waypoints.stream().anyMatch(waypoint -> waypoint.getId() ==  waypointId);
    }

    public List<Waypoint> getWaypoints() {
        return Collections.unmodifiableList(this.waypoints);
    }
}
