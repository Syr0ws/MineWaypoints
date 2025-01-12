package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.plugincrafter.message.placeholder.Placeholder;

public enum CustomPlaceholder implements Placeholder {

    WAYPOINT_NAME,
    WAYPOINT_COORD_X,
    WAYPOINT_COORD_Y,
    WAYPOINT_COORD_Z,
    WAYPOINT_WORLD,
    WAYPOINT_OLD_NAME,
    WAYPOINT_OLD_COORD_X,
    WAYPOINT_OLD_COORD_Y,
    WAYPOINT_OLD_COORD_Z,
    WAYPOINT_OLD_WORLD,
    TARGET_NAME;

    @Override
    public String getName() {
        return "%" + name().toLowerCase() + "%";
    }
}
