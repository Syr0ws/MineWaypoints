package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.exception.InventoryConfigException;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.common.config.yaml.YamlCommonActionLoader;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointActivationService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class ToggleWaypointActivationLoader extends YamlCommonActionLoader {

    private final Plugin plugin;
    private final BukkitWaypointActivationService waypointActivationService;

    public ToggleWaypointActivationLoader(Plugin plugin, BukkitWaypointActivationService waypointActivationService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.plugin = plugin;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    public ClickAction load(ConfigurationSection section) throws InventoryConfigException {
        Set<ClickType> clickTypes = super.loadClickTypes(section);
        return new ToggleWaypointActivation(clickTypes, this.plugin, this.waypointActivationService);
    }

    @Override
    public String getName() {
        return ToggleWaypointActivation.ACTION_NAME;
    }
}
