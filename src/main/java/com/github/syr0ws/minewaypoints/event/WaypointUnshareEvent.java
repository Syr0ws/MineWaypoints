package com.github.syr0ws.minewaypoints.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

public class WaypointUnshareEvent extends WaypointEvent {

    private final WaypointUser sharedWith;

    public WaypointUnshareEvent(Waypoint waypoint, WaypointUser sharedWith) {
        super(waypoint);

        Validate.notNull(sharedWith, "sharedWith cannot be null");
        this.sharedWith = sharedWith;
    }

    public WaypointUser getSharedWith() {
        return this.sharedWith;
    }
}
