package com.github.syr0ws.minewaypoints.model;

import org.bukkit.Material;

import java.util.Date;

public class WaypointModel {

    private final long id;
    private final WaypointUser owner;
    private final Date createdAt;

    private String name;
    private Material icon;
    private WaypointLocation location;

    public WaypointModel(long id, WaypointUser owner, Date createdAt, String name, Material icon, WaypointLocation location) {
        this.id = id;
        this.owner = owner;
        this.createdAt = createdAt;
        this.setName(name);
        this.setIcon(icon);
        this.setLocation(location);
    }

    public long getId() {
        return this.id;
    }

    public WaypointUser getOwner() {
        return this.owner;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }

        this.name = name;
    }

    public Material getIcon() {
        return this.icon;
    }

    public void setIcon(Material icon) {

        if (icon == null || icon.isAir()) {
            throw new IllegalArgumentException("icon cannot be null or empty");
        }

        this.icon = icon;
    }

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
