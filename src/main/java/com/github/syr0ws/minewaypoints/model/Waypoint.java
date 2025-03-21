package com.github.syr0ws.minewaypoints.model;

import java.util.Date;

public interface Waypoint {

    long getId();

    WaypointUser getOwner();

    Date getCreatedAt();

    String getName();

    String getIcon();

    WaypointLocation getLocation();
}
