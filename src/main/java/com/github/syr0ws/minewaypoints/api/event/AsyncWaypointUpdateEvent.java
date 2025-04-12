package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player attempts to update one of its waypoint.
 */
public class AsyncWaypointUpdateEvent extends WaypointEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player owner;
    private String newWaypointName;
    private Location newLocation;
    private Material newIcon;

    private boolean cancelled;

    public AsyncWaypointUpdateEvent(Player owner, Waypoint waypoint, String newWaypointName, Location newLocation, Material newIcon) {
        super(waypoint, true);
        this.owner = owner;
        this.setNewWaypointName(newWaypointName);
        this.setNewLocation(newLocation);
        this.setNewIcon(newIcon);
    }

    /**
     * Returns the player who is updating the waypoint.
     *
     * @return the owner of the waypoint
     */
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Returns the new name of the waypoint.
     *
     * @return the new waypoint name
     */
    public String getNewWaypointName() {
        return this.newWaypointName;
    }

    /**
     * Sets the new name of the waypoint.
     *
     * @param newWaypointName the waypoint name
     * @throws NullPointerException if {@code newWaypointName} is {@code null} or empty
     */
    public void setNewWaypointName(String newWaypointName) {
        Validate.notEmpty(newWaypointName, "newWaypointName cannot be null or empty");
        this.newWaypointName = newWaypointName;
    }

    /**
     * Returns the new location of the waypoint.
     *
     * @return the new waypoint location
     */
    public Location getNewLocation() {
        return this.newLocation;
    }

    /**
     * Sets the new location of the waypoint.
     *
     * @param newLocation the new waypoint location
     * @throws NullPointerException if {@code location} is {@code null}
     */
    public void setNewLocation(Location newLocation) {
        Validate.notNull(newLocation, "newLocation cannot be null");
        this.newLocation = newLocation;
    }

    /**
     * Returns the new icon of the waypoint.
     *
     * @return the new icon of the waypoint
     */
    public Material getNewIcon() {
        return this.newIcon;
    }

    /**
     * Sets the new icon of the waypoint.
     *
     * @param newIcon the new icon of the waypoint
     */
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
