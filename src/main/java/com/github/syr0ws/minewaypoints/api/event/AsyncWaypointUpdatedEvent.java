package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a waypoint has been updated its owner.
 */
public class AsyncWaypointUpdatedEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player owner;

    public AsyncWaypointUpdatedEvent(Waypoint waypoint, Player owner) {
        super(waypoint, true);
        this.owner = owner;
    }

    /**
     * Returns the player who has updated the waypoint.
     *
     * @return the owner of the waypoint
     */
    public Player getOwner() {
        return this.owner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
