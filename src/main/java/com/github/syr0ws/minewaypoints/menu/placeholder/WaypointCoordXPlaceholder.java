package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;

public class WaypointCoordXPlaceholder extends WaypointCoordinatePlaceholder {

    @Override
    public String getName() {
        return "%waypoint_coord_x%";
    }

    @Override
    protected double getCoordiate(Context context) {
        Waypoint waypoint = super.getWaypoint(context);
        return waypoint.getLocation().getX();
    }
}
