package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.exception.InventoryConfigException;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.common.config.yaml.YamlCommonActionLoader;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class DeleteWaypointLoader extends YamlCommonActionLoader {

    private final Plugin plugin;
    private final WaypointService waypointService;

    public DeleteWaypointLoader(Plugin plugin, WaypointService waypointService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public ClickAction load(ConfigurationSection section) throws InventoryConfigException {
        Set<ClickType> clickTypes = super.loadClickTypes(section);
        return new DeleteWaypoint(clickTypes, this.plugin, this.waypointService);
    }

    @Override
    public String getName() {
        return DeleteWaypoint.ACTION_NAME;
    }
}
