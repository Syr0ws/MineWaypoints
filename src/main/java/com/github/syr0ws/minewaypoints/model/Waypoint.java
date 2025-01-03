package com.github.syr0ws.minewaypoints.model;

import org.bukkit.Material;

import java.util.Date;

public interface Waypoint {

    long getId();

    WaypointUser getOwner();

    Date getCreatedAt();

    String getName();

    Material getIcon();

    WaypointLocation getLocation();
}
