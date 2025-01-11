package com.github.syr0ws.minewaypoints.model.entity;

import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.util.Date;

public class WaypointShareEntity implements WaypointShare {

    private final WaypointUser sharedWith;
    private final WaypointEntity waypoint;
    private final Date sharedAt;

    public WaypointShareEntity(WaypointUser sharedWith, WaypointEntity waypoint, Date sharedAt) {
        this.sharedWith = sharedWith;
        this.waypoint = waypoint;
        this.sharedAt = sharedAt;
    }

    @Override
    public WaypointUser getSharedWith() {
        return this.sharedWith;
    }

    @Override
    public WaypointEntity getWaypoint() {
        return this.waypoint;
    }

    @Override
    public Date getSharedAt() {
        return this.sharedAt;
    }
}
