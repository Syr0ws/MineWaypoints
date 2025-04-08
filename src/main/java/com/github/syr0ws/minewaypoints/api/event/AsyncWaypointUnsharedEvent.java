package com.github.syr0ws.minewaypoints.api.event;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import org.bukkit.event.HandlerList;

public class AsyncWaypointUnsharedEvent extends WaypointEvent {

    private static final HandlerList handlers = new HandlerList();

    private final WaypointUser sharedWith;

    public AsyncWaypointUnsharedEvent(Waypoint waypoint, WaypointUser sharedWith) {
        super(waypoint, true);

        Validate.notNull(sharedWith, "sharedWith cannot be null");
        this.sharedWith = sharedWith;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WaypointUser getSharedWith() {
        return this.sharedWith;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
