package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointUser;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a waypoint is no longer shared with a player.
 */
public class AsyncWaypointUnsharedEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    private final WaypointUser sharedWith;
    private final Player player;

    public AsyncWaypointUnsharedEvent(Waypoint waypoint, WaypointUser sharedWith, Player player) {
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
     * Returns the user the waypoint was previously shared with.
     *
     * @return the {@link WaypointUser} the waypoint was unshared from
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
    public HandlerList getHandlers() {
        return handlers;
    }
}
