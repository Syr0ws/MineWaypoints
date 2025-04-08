package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncWaypointCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player owner;
    private String waypointName;
    private Location location;
    private Material icon;

    private boolean cancelled;

    public AsyncWaypointCreateEvent(Player owner, String waypointName, Location location, Material icon) {
        super(true);
        Validate.notNull(owner, "owner cannot be null");
        this.owner = owner;
        this.setWaypointName(waypointName);
        this.setLocation(location);
        this.setIcon(icon);
    }

    public Player getOwner() {
        return this.owner;
    }

    public String getWaypointName() {
        return this.waypointName;
    }

    public void setWaypointName(String waypointName) {
        Validate.notNull(waypointName, "waypointName cannot be null or empty");
        this.waypointName = waypointName;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        Validate.notNull(location, "location cannot be null");
        this.location = location;
    }

    public Material getIcon() {
        return this.icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
