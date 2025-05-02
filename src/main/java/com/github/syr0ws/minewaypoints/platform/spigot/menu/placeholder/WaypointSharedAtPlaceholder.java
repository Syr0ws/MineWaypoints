package com.github.syr0ws.minewaypoints.platform.spigot.menu.placeholder;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointShare;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WaypointSharedAtPlaceholder extends WaypointSharePlaceholder {

    private final Plugin plugin;

    public WaypointSharedAtPlaceholder(Plugin plugin) {
        Validate.notNull(plugin, "plugin cannot be null");

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
