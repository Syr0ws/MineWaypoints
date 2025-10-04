package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.crafter.config.ConfigurationMap;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.common.config.yaml.YamlCommonActionLoader;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class UpdateWaypointIconLoader extends YamlCommonActionLoader {

    private final Plugin plugin;
    private final BukkitWaypointService waypointService;

    public UpdateWaypointIconLoader(Plugin plugin, BukkitWaypointService waypointService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public ClickAction load(ConfigurationMap map) {
        return new UpdateWaypointIcon(this.plugin, this.waypointService);
    }

    @Override
    public String getName() {
        return UpdateWaypointIcon.ACTION_NAME;
    }
}
