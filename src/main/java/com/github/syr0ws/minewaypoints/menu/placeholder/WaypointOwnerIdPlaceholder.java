package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;

public class WaypointOwnerIdPlaceholder extends WaypointPlaceholder {

    @Override
    public String getName() {
        return "%waypoint_owner_id%";
    }

    @Override
    public String getValue(Context context) {
        Waypoint waypoint = super.getWaypoint(context);
        return waypoint.getOwner().getId().toString();
    }
}
