package com.github.syr0ws.minewaypoints.model;

import java.util.Date;

public class WaypointShare {

    private final Waypoint waypoint;
    private final Date sharedAt;

    public WaypointShare(Waypoint waypoint, Date sharedAt) {
        this.waypoint = waypoint;
        this.sharedAt = sharedAt;
    }

    public Waypoint getWaypoint() {
        return this.waypoint;
    }

    public Date getSharedAt() {
        return this.sharedAt;
    }
}
