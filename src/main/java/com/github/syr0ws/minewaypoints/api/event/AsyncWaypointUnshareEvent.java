package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player is trying to stop sharing a waypoint.
 */
public class AsyncWaypointUnshareEvent extends WaypointEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final WaypointUser sharedWith;
    private final Player player;
    private boolean cancelled;

    public AsyncWaypointUnshareEvent(Waypoint waypoint, WaypointUser sharedWith, Player player) {
        super(waypoint, true);
        Validate.notNull(sharedWith, "sharedWith cannot be null");
        Validate.notNull(player, "player cannot be null");
        this.sharedWith = sharedWith;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the user the waypoint is shared with.
     *
     * @return the {@link WaypointUser} the waypoint is unshared from
     */
    public WaypointUser getSharedWith() {
        return this.sharedWith;
    }

    /**
     * Returns the player who is doing the action.
     *
     * @return the waypoint owner or the player the waypoint is shared with
     */
    public Player getPlayer() {
        return this.player;
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
