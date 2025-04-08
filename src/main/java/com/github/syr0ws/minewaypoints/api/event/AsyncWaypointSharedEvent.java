package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class AsyncWaypointSharedEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player target;

    public AsyncWaypointSharedEvent(Waypoint waypoint, Player target) {
        super(waypoint, true);
        Validate.notNull(target, "target cannot be null");
        this.target = target;
    }

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
