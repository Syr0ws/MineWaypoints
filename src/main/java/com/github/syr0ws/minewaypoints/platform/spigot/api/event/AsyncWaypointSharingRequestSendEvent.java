package com.github.syr0ws.minewaypoints.platform.spigot.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when the owner of a waypoint sends a sharing request to a player.
 */
public class AsyncWaypointSharingRequestSendEvent extends WaypointEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player owner;
    private final Player target;
    private boolean cancelled;

    public AsyncWaypointSharingRequestSendEvent(Waypoint waypoint, Player owner, Player target) {
        super(waypoint, true);
        Validate.notNull(owner, "owner cannot be null");
        Validate.notNull(target, "target cannot be null");
        this.owner = owner;
        this.target = target;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the player who is sending the sharing request.
     *
     * @return the owner of the waypoint
     */
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Returns the player who is receiving the sharing request.
     *
     * @return the target player
     */
    public Player getTarget() {
        return this.target;
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
