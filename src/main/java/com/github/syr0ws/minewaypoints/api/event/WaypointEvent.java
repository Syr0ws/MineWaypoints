package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.event.Event;

/**
 * Represents a generic waypoint event.
 */
public abstract class WaypointEvent extends Event {

    private final Waypoint waypoint;

    public WaypointEvent(Waypoint waypoint, boolean isAsync) {
        super(isAsync);

        Validate.notNull(waypoint, "waypoint cannot be null");
        this.waypoint = waypoint;
    }

    /**
     * Returns the waypoint involved in this event.
     *
     * @return the {@link Waypoint} associated with the event
     */
    public Waypoint getWaypoint() {
        return this.waypoint;
    }
}
