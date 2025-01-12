package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.plugincrafter.message.placeholder.Placeholder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class PlaceholderUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static Map<Placeholder, String> getWaypointPlaceholders(Waypoint waypoint) {

        Map<Placeholder, String> placeholders = new HashMap<>();

        WaypointLocation location = waypoint.getLocation();

        placeholders.put(CustomPlaceholder.WAYPOINT_NAME, waypoint.getName());
        placeholders.put(CustomPlaceholder.WAYPOINT_COORD_X, DECIMAL_FORMAT.format(location.getX()));
        placeholders.put(CustomPlaceholder.WAYPOINT_COORD_Y, DECIMAL_FORMAT.format(location.getY()));
        placeholders.put(CustomPlaceholder.WAYPOINT_COORD_Z, DECIMAL_FORMAT.format(location.getZ()));
        placeholders.put(CustomPlaceholder.WAYPOINT_WORLD, location.getWorld());

        return placeholders;
    }

    public static Map<Placeholder, String> getWaypointOldLocationPlaceholders(WaypointLocation location) {

        Map<Placeholder, String> placeholders = new HashMap<>();

        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_COORD_X, DECIMAL_FORMAT.format(location.getX()));
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_COORD_Y, DECIMAL_FORMAT.format(location.getY()));
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_COORD_Z, DECIMAL_FORMAT.format(location.getZ()));
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_WORLD, location.getWorld());

        return placeholders;
    }
}
