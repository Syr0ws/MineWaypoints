package com.github.syr0ws.minewaypoints.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.util.Collections;
import java.util.Set;

public class WaypointDeleteEvent extends WaypointEvent {

    private final Set<WaypointUser> waypointSharedWith;

    public WaypointDeleteEvent(Waypoint waypoint, Set<WaypointUser> waypointSharedWith) {
        super(waypoint);

        Validate.notNull(waypointSharedWith, "waypointSharedWith cannot be null");
        this.waypointSharedWith = Collections.unmodifiableSet(waypointSharedWith);
    }

    public Set<WaypointUser> getWaypointSharedWith() {
        return this.waypointSharedWith;
    }
}
