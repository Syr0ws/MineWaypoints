package com.github.syr0ws.minewaypoints.util.placeholder;

import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class PlaceholderUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static Map<Placeholder, String> getWaypointPlaceholders(Plugin plugin, Waypoint waypoint) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypoint, "waypoint cannot be null");

        Map<Placeholder, String> placeholders = new HashMap<>();

        WaypointLocation location = waypoint.getLocation();

        placeholders.put(CustomPlaceholder.WAYPOINT_NAME, waypoint.getName());
        placeholders.put(CustomPlaceholder.WAYPOINT_COORD_X, DECIMAL_FORMAT.format(location.getX()));
        placeholders.put(CustomPlaceholder.WAYPOINT_COORD_Y, DECIMAL_FORMAT.format(location.getY()));
        placeholders.put(CustomPlaceholder.WAYPOINT_COORD_Z, DECIMAL_FORMAT.format(location.getZ()));
        placeholders.put(CustomPlaceholder.WAYPOINT_WORLD, location.getWorld());
        placeholders.put(CustomPlaceholder.WAYPOINT_OWNER_NAME, waypoint.getOwner().getName());

        FileConfiguration config = plugin.getConfig();
        DateFormat format = new SimpleDateFormat(config.getString("date-format", "yyyy/MM/dd"));

        placeholders.put(CustomPlaceholder.WAYPOINT_CREATED_AT, format.format(waypoint.getCreatedAt()));

        return placeholders;
    }

    public static Map<Placeholder, String> getWaypointOldLocationPlaceholders(WaypointLocation location) {
        Validate.notNull(location, "location cannot be null");

        Map<Placeholder, String> placeholders = new HashMap<>();

        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_COORD_X, DECIMAL_FORMAT.format(location.getX()));
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_COORD_Y, DECIMAL_FORMAT.format(location.getY()));
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_COORD_Z, DECIMAL_FORMAT.format(location.getZ()));
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_WORLD, location.getWorld());

        return placeholders;
    }
}
