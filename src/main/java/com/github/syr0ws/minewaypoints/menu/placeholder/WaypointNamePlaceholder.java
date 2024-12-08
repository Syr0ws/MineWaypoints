package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;

public class WaypointNamePlaceholder implements Placeholder  {

    @Override
    public String getName() {
        return "%waypoint_name%";
    }

    @Override
    public String getValue(Context context) {
        Waypoint waypoint = context.getData(CommonContextKey.PAGINATION_ITEM.name(), Waypoint.class);
        return waypoint.getName();
    }

    @Override
    public boolean accept(Context context) {
        return context.hasData(CommonContextKey.PAGINATION_ITEM.name());
    }
}
