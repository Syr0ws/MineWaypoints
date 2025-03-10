package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

public class WaypointSharedWithUserNamePlaceholder extends WaypointSharePlaceholder {

    @Override
    public String getName() {
        return "%shared_with_user_name%";
    }

    @Override
    public String getValue(Context context) {
        WaypointShare share = super.getWaypointShare(context);
        WaypointUser with = share.getSharedWith();
        return with.getName();
    }
}
