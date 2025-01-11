package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WaypointSharedAtPlaceholder extends WaypointSharePlaceholder {

    private final Plugin plugin;

    public WaypointSharedAtPlaceholder(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "%shared_at%";
    }

    @Override
    public String getValue(Context context) {

        WaypointShare share = super.getWaypointShare(context);

        FileConfiguration config = this.plugin.getConfig();
        DateFormat format = new SimpleDateFormat(config.getString("date-format", "yyyy/MM/dd"));

        return format.format(share.getSharedAt());
    }
}
