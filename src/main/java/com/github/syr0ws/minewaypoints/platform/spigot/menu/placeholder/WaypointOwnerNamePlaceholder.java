package com.github.syr0ws.minewaypoints.platform.spigot.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;

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
