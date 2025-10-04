package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.crafter.config.ConfigurationMap;
import com.github.syr0ws.craftventory.api.config.exception.InventoryConfigException;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.common.config.yaml.YamlCommonActionLoader;

import java.util.Set;

public class OpenSharedWaypointDeleteMenuLoader extends YamlCommonActionLoader {

    @Override
    public ClickAction load(ConfigurationMap map) throws InventoryConfigException {
        Set<ClickType> clickTypes = super.loadClickTypes(map);
        return new OpenSharedWaypointDeleteMenu(clickTypes);
    }

    @Override
    public String getName() {
        return OpenSharedWaypointDeleteMenu.ACTION_NAME;
    }
}
