package com.github.syr0ws.minewaypoints.model;

import java.util.Date;

public class WaypointShareModel implements WaypointShare {

    private final Waypoint waypoint;
    private final Date sharedAt;

    public WaypointShareModel(Waypoint waypoint, Date sharedAt) {
        this.waypoint = waypoint;
        this.sharedAt = sharedAt;
    }

    @Override
    public Waypoint getWaypoint() {
        return this.waypoint;
    }

    @Override
    public Date getSharedAt() {
        return this.sharedAt;
    }
}
