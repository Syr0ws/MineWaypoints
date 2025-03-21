package com.github.syr0ws.minewaypoints.model.entity;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.util.Date;

public class WaypointEntity implements Waypoint {

    private final long waypointId;
    private final WaypointUser owner;
    private final Date createdAt;

    private String name;
    private String icon;
    private WaypointLocation location;

    public WaypointEntity(long waypointId, WaypointUser owner, Date createdAt, String name, String icon, WaypointLocation location) {
        this.waypointId = waypointId;
        this.owner = owner;
        this.createdAt = createdAt;
        this.setName(name);
        this.setIcon(icon);
        this.setLocation(location);
    }

    @Override
    public long getId() {
        return this.waypointId;
    }

    @Override
    public WaypointUser getOwner() {
        return this.owner;
    }

    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }

        this.name = name;
    }

    @Override
    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        Validate.notEmpty(icon, "icon cannot be null or empty");
        this.icon = icon;
    }

    @Override
    public WaypointLocation getLocation() {
        return this.location;
    }

    public void setLocation(WaypointLocation location) {

        if (location == null) {
            throw new IllegalArgumentException("location cannot be null");
        }

        this.location = location;
    }
}
