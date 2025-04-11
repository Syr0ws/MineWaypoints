package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a waypoint has been shared with a player.
 */
public class AsyncWaypointSharedEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player target;

    public AsyncWaypointSharedEvent(Waypoint waypoint, Player target) {
        super(waypoint, true);
        Validate.notNull(target, "target cannot be null");
        this.target = target;
    }

    /**
     * Returns the player the waypoint has been shared with.
     *
     * @return the target player
     */
    public Player getTarget() {
        return this.target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
