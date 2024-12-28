package com.github.syr0ws.minewaypoints.model;

import java.util.Date;

public class WaypointShare {

    private final long waypointId;
    private final Date sharedAt;

    public WaypointShare(long waypointId, Date sharedAt) {
        this.waypointId = waypointId;
        this.sharedAt = sharedAt;
    }

    public long getWaypointId() {
        return this.waypointId;
    }

    public Date getSharedAt() {
        return this.sharedAt;
    }
}
