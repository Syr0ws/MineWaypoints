package com.github.syr0ws.minewaypoints.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class Waypoint {

    private final long id;
    private final WaypointUser owner;

    private String name;
    private Material icon;
    private String world;
    private double coordX, coordY, coordZ;

    public Waypoint(WaypointUser owner, Location location, Material icon, String name) {

        if (owner == null) {
            throw new IllegalArgumentException("owner cannot be null");
        }

        this.id = 0;
        this.owner = owner;

        this.setName(name);
        this.setIcon(icon);
        this.setLocation(location);
    }

    public Waypoint(long id, WaypointUser owner, String name, Material icon, String world, double coordX, double coordY, double coordZ) {

        if (owner == null) {
            throw new IllegalArgumentException("owner cannot be null");
        }

        if (world == null) {
            throw new IllegalArgumentException("world cannot be null");
        }

        this.id = id;
        this.owner = owner;

        this.world = world;
        this.coordX = coordX;
        this.coordY = coordY;
        this.coordZ = coordZ;

        this.setName(name);
        this.setIcon(icon);
    }

    public long getId() {
        return this.id;
    }

    public WaypointUser getOwner() {
        return this.owner;
    }

    public String getWorld() {
        return this.world;
    }

    public double getX() {
        return this.coordX;
    }

    public double getY() {
        return this.coordY;
    }

    public double getZ() {
        return this.coordZ;
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

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.coordX, this.coordY, this.coordZ);
    }

    public void setLocation(Location location) {

        if (location == null) {
            throw new IllegalArgumentException("location cannot be null");
        }

        this.world = location.getWorld().getName();
        this.coordX = location.getX();
        this.coordY = location.getY();
        this.coordZ = location.getZ();
    }
}
