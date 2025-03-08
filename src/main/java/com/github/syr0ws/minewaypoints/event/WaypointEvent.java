package com.github.syr0ws.minewaypoints.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class WaypointEvent extends Event {

    private final Waypoint waypoint;

    public WaypointEvent(Waypoint waypoint) {
        Validate.notNull(waypoint, "waypoint cannot be null");
        this.waypoint = waypoint;
    }

    public Waypoint getWaypoint() {
        return this.waypoint;
    }
}
