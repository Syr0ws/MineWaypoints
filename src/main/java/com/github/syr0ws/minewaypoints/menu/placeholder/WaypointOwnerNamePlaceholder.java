package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.model.Waypoint;

public class WaypointOwnerNamePlaceholder extends WaypointPlaceholder {

    @Override
    public String getName() {
        return "%waypoint_owner_name%";
    }

    @Override
    public String getValue(Context context) {
        Waypoint waypoint = super.getWaypoint(context);
        return waypoint.getOwner().getName();
    }
}
