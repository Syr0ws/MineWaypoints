package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WaypointCreatedAtPlaceholder extends WaypointPlaceholder {

    private final Plugin plugin;

    public WaypointCreatedAtPlaceholder(Plugin plugin) {
        Validate.notNull(plugin, "plugin cannot be null");

        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "%waypoint_created_at%";
    }

    @Override
    public String getValue(Context context) {

        Waypoint waypoint = super.getWaypoint(context);

        FileConfiguration config = this.plugin.getConfig();
        DateFormat format = new SimpleDateFormat(config.getString("date-format", "yyyy/MM/dd"));

        return format.format(waypoint.getCreatedAt());
    }
}
