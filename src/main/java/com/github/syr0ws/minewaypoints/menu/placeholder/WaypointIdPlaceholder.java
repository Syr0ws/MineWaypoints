package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.model.Waypoint;

public class WaypointIdPlaceholder extends WaypointPlaceholder {

    @Override
    public String getName() {
        return "%waypoint_id%";
    }

    @Override
    public String getValue(Context context) {
        Waypoint waypoint = super.getWaypoint(context);
        return Long.toString(waypoint.getId());
    }
}
