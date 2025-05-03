package com.github.syr0ws.minewaypoints.plugin.domain;

import java.util.Date;

public interface WaypointShare {

    WaypointUser getSharedWith();

    Waypoint getWaypoint();

    Date getSharedAt();
}
