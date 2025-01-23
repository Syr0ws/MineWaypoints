package com.github.syr0ws.minewaypoints.util.placeholder;

import com.github.syr0ws.crafter.message.placeholder.Placeholder;

public enum CustomPlaceholder implements Placeholder {

    WAYPOINT_NAME,
    WAYPOINT_COORD_X,
    WAYPOINT_COORD_Y,
    WAYPOINT_COORD_Z,
    WAYPOINT_WORLD,
    WAYPOINT_CREATED_AT,
    WAYPOINT_OWNER_NAME,
    WAYPOINT_OLD_NAME,
    WAYPOINT_OLD_COORD_X,
    WAYPOINT_OLD_COORD_Y,
    WAYPOINT_OLD_COORD_Z,
    WAYPOINT_OLD_WORLD,
    TARGET_NAME,
    SHARE_REQUEST_ID;

    @Override
    public String getName() {
        return "%" + name().toLowerCase() + "%";
    }
}
