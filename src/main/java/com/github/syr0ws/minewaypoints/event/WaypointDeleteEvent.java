package com.github.syr0ws.minewaypoints.event;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.event.HandlerList;

public class WaypointDeleteEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    public WaypointDeleteEvent(Waypoint waypoint) {
        super(waypoint);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
