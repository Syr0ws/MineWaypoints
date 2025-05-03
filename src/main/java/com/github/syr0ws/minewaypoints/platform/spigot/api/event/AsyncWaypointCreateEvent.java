package com.github.syr0ws.minewaypoints.platform.spigot.api.event;

import com.github.syr0ws.crafter.util.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player attempts to create a new waypoint.
 */
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the player who is creating the waypoint.
     *
     * @return the owner of the waypoint
     */
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Returns the name of the waypoint.
     *
     * @return the waypoint name
     */
    public String getWaypointName() {
        return this.waypointName;
    }

    /**
     * Sets the name of the waypoint.
     *
     * @param waypointName the waypoint name
     * @throws NullPointerException if {@code waypointName} is {@code null}
     */
    public void setWaypointName(String waypointName) {
        Validate.notNull(waypointName, "waypointName cannot be null or empty");
        this.waypointName = waypointName;
    }

    /**
     * Returns the location of the waypoint.
     *
     * @return the waypoint location
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Sets the location of the waypoint.
     *
     * @param location the waypoint location
     * @throws NullPointerException if {@code location} is {@code null}
     */
    public void setLocation(Location location) {
        Validate.notNull(location, "location cannot be null");
        this.location = location;
    }

    /**
     * Returns the icon of the waypoint.
     *
     * @return the icon of the waypoint
     */
    public Material getIcon() {
        return this.icon;
    }

    /**
     * Sets the icon of the waypoint.
     *
     * @param icon the icon of the waypoint
     */
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
}
