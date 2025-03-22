package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.exception.InventoryConfigException;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.common.config.yaml.YamlCommonActionLoader;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class UnshareWaypointLoader extends YamlCommonActionLoader {

    private final Plugin plugin;
    private final BukkitWaypointService waypointService;

    public UnshareWaypointLoader(Plugin plugin, BukkitWaypointService waypointService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public ClickAction load(ConfigurationSection section) throws InventoryConfigException {
        Set<ClickType> clickTypes = super.loadClickTypes(section);
        return new UnshareWaypoint(clickTypes, this.plugin, this.waypointService);
    }

    @Override
    public String getName() {
        return UnshareWaypoint.ACTION_NAME;
    }
}
