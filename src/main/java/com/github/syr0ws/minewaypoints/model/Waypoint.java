package com.github.syr0ws.minewaypoints.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class Waypoint {

    private final long id;
    private final String world;
    private final double x, y, z;

    private String name;
    private Material icon;
    private final boolean activated;

    public Waypoint(long id, String world, double x, double y, double z, String name, Material icon, boolean activated) {
        this.id = id;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.setName(name);
        this.setIcon(icon);
        this.activated = activated;
    }

    public long getId() {
        return this.id;
    }

    public String getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
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

    public boolean isActivated() {
        return this.activated;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }
}
