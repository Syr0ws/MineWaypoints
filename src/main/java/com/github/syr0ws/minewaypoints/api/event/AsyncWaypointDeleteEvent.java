package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class AsyncWaypointDeleteEvent extends WaypointEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player owner;
    private boolean cancelled;

    public AsyncWaypointDeleteEvent(Waypoint waypoint, Player owner) {
        super(waypoint, true);
        Validate.notNull(owner, "owner cannot be null");
        this.owner = owner;
    }

    public Player getOwner() {
        return this.owner;
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
