package com.github.syr0ws.minewaypoints.event;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.event.HandlerList;

public class WaypointDeleteEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    public WaypointDeleteEvent(Waypoint waypoint) {
        super(waypoint);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
