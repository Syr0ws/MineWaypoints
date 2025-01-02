package com.github.syr0ws.minewaypoints.model;

import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import org.bukkit.Material;

import java.util.Date;

public class WaypointModel implements Waypoint {

    private final long id;
    private final WaypointUserModel owner;
    private final Date createdAt;
    private final WaypointUserCache<WaypointUserModel> waypointUserCache;

    private String name;
    private Material icon;
    private WaypointLocation location;

    public WaypointModel(long id, WaypointUserModel owner, Date createdAt, String name, Material icon, WaypointLocation location, WaypointUserCache<WaypointUserModel> waypointUserCache) {
        this.id = id;
        this.owner = owner;
        this.createdAt = createdAt;
        this.waypointUserCache = waypointUserCache;
        this.setName(name);
        this.setIcon(icon);
        this.setLocation(location);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public WaypointUserModel getOwner() {
        return this.waypointUserCache.getUser(this.owner.getId()).orElse(this.owner);
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
    public Material getIcon() {
        return this.icon;
    }

    public void setIcon(Material icon) {

        if (icon == null || icon.isAir()) {
            throw new IllegalArgumentException("icon cannot be null or empty");
        }

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
