package com.github.syr0ws.minewaypoints.platform.spigot.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a waypoint has been created by a player.
 */
public class AsyncWaypointCreatedEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player owner;

    public AsyncWaypointCreatedEvent(Waypoint waypoint, Player owner) {
        super(waypoint, true);
        Validate.notNull(owner, "owner cannot be null");
        this.owner = owner;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the player who created the waypoint.
     *
     * @return the waypoint's owner
     */
    public Player getOwner() {
        return this.owner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
