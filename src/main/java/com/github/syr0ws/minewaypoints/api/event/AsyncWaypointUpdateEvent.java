package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.event.HandlerList;

public class AsyncWaypointUpdateEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    public AsyncWaypointUpdateEvent(Waypoint waypoint) {
        super(waypoint, true);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
