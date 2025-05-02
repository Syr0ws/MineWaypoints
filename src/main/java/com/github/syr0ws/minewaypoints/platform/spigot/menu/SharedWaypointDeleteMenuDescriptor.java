package com.github.syr0ws.minewaypoints.platform.spigot.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.hook.WaypointShareInitStoreHook;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.util.PlaceholderUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SharedWaypointDeleteMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "shared-waypoint-delete-menu";
    private static final String MENU_CONFIG_PATH = "menus/shared-waypoint-delete-menu.yml";

    public SharedWaypointDeleteMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {
        super(plugin, inventoryConfigDAO);
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
        PlaceholderUtil.addWaypointSharePlaceholders(manager, this.getPlugin());
    }

    @Override
    public void addHooks(HookManager manager) {
        manager.addHook(WaypointShareInitStoreHook.HOOK_ID, CraftVentoryBeforeOpenEvent.class, new WaypointShareInitStoreHook());
    }

    @Override
    public String getInventoryResourceFile() {
        return MENU_CONFIG_PATH;
    }

    @Override
    public Path getInventoryConfigFile() {
        return Paths.get(super.getPlugin().getDataFolder() + File.separator + MENU_CONFIG_PATH);
    }

    @Override
    public String getInventoryId() {
        return MENU_ID;
    }
}
