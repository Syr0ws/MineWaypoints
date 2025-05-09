package com.github.syr0ws.minewaypoints.platform.spigot.api.event;

import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
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

    public static HandlerList getHandlerList() {
        return handlers;
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
}
