package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.model.Waypoint;

public class WaypointCoordZPlaceholder extends WaypointCoordinatePlaceholder {

    @Override
    public String getName() {
        return "%waypoint_coord_z%";
    }

    @Override
    protected double getCoordiate(Context context) {
        Waypoint waypoint = super.getWaypoint(context);
        return waypoint.getLocation().getZ();
    }
}
