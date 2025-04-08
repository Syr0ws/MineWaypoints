package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncWaypointUpdateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player owner;
    private String newWaypointName;
    private Location newLocation;
    private Material newIcon;

    private boolean cancelled;

    public AsyncWaypointUpdateEvent(Player owner, String newWaypointName, Location newLocation, Material newIcon) {
        super(true);
        this.owner = owner;
        this.setNewWaypointName(newWaypointName);
        this.setNewLocation(newLocation);
        this.setNewIcon(newIcon);
    }

    public Player getOwner() {
        return this.owner;
    }

    public String getNewWaypointName() {
        return this.newWaypointName;
    }

    public void setNewWaypointName(String newWaypointName) {
        Validate.notEmpty(newWaypointName, "newWaypointName cannot be null or empty");
        this.newWaypointName = newWaypointName;
    }

    public Location getNewLocation() {
        return this.newLocation;
    }

    public void setNewLocation(Location newLocation) {
        Validate.notNull(newLocation, "newLocation cannot be null");
        this.newLocation = newLocation;
    }

    public Material getNewIcon() {
        return this.newIcon;
    }

    public void setNewIcon(Material newIcon) {
        this.newIcon = newIcon;
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
