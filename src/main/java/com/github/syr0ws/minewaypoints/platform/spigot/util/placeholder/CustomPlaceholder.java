package com.github.syr0ws.minewaypoints.platform.spigot.util.placeholder;

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
    SHARE_REQUEST_ID,
    SHARED_WITH_NAME,
    SHARED_AT,
    WAYPOINT_DIRECTION,
    WAYPOINT_DISTANCE;

    @Override
    public String getName() {
        return "%" + name().toLowerCase() + "%";
    }
}
