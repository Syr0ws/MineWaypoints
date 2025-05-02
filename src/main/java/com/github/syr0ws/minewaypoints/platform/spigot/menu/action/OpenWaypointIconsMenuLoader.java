package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.craftventory.api.config.exception.InventoryConfigException;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.common.config.yaml.YamlCommonActionLoader;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class OpenWaypointIconsMenuLoader extends YamlCommonActionLoader {

    @Override
    public ClickAction load(ConfigurationSection section) throws InventoryConfigException {
        Set<ClickType> clickTypes = super.loadClickTypes(section);
        return new OpenWaypointIconsMenu(clickTypes);
    }

    @Override
    public String getName() {
        return OpenWaypointIconsMenu.ACTION_NAME;
    }
}
