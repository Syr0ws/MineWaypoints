package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

public class WaypointSharedToUserIdPlaceholder extends WaypointSharePlaceholder {

    @Override
    public String getName() {
        return "%shared_to_user_id%";
    }

    @Override
    public String getValue(Context context) {
        WaypointShare share = super.getWaypointShare(context);
        WaypointUser to = share.getSharedWith();
        return to.getId().toString();
    }
}
