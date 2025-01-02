package com.github.syr0ws.minewaypoints.model;

import com.github.syr0ws.minewaypoints.cache.WaypointCache;

import java.util.Date;

public class WaypointShareModel implements WaypointShare {

    private final long waypointId;
    private final Date sharedAt;
    private final WaypointCache<WaypointModel> waypointCache;

    public WaypointShareModel(long waypointId, Date sharedAt, WaypointCache<WaypointModel> waypointCache) {
        this.waypointId = waypointId;
        this.sharedAt = sharedAt;
        this.waypointCache = waypointCache;
    }

    @Override
    public Waypoint getWaypoint() {
        return this.waypointCache.getWaypoint(this.waypointId)
                .orElseThrow(() -> new NullPointerException("Waypoint not found"));
    }

    @Override
    public Date getSharedAt() {
        return this.sharedAt;
    }
}
