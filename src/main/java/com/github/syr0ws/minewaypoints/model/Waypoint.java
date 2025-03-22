package com.github.syr0ws.minewaypoints.model;

import java.util.Date;

public interface Waypoint {

    long getId();

    WaypointUser getOwner();

    Date getCreatedAt();

    String getName();

    void setName(String name);

    String getIcon();

    void setIcon(String icon);

    WaypointLocation getLocation();

    void setLocation(WaypointLocation location);
}
