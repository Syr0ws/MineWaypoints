package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.config.exception.InventoryConfigException;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.common.config.yaml.YamlCommonActionLoader;
import org.bukkit.configuration.ConfigurationSection;

public class UpdateWaypointIconLoader extends YamlCommonActionLoader {

    @Override
    public ClickAction load(ConfigurationSection section) throws InventoryConfigException {
        return new UpdateWaypointIcon();
    }

    @Override
    public String getName() {
        return UpdateWaypointIcon.ACTION_NAME;
    }
}
