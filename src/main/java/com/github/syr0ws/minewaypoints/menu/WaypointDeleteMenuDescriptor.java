package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.minewaypoints.menu.hook.WaypointInitStoreHook;
import com.github.syr0ws.minewaypoints.menu.placeholder.WaypointPlaceholderEnum;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class WaypointDeleteMenuDescriptor implements InventoryDescriptor {

    public static final String MENU_ID = "waypoint-delete-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoint-delete-menu.yml";

    private final Plugin plugin;
    private final InventoryConfigDAO inventoryConfigDAO;

    public WaypointDeleteMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {
        this.plugin = plugin;
        this.inventoryConfigDAO = inventoryConfigDAO;
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        Arrays.stream(WaypointPlaceholderEnum.values())
                .forEach(placeholder -> manager.addPlaceholder(placeholder.get()));
    }

    @Override
    public void addHooks(HookManager manager) {
        manager.addHook(WaypointInitStoreHook.HOOK_ID, CraftVentoryBeforeOpenEvent.class, new WaypointInitStoreHook());
    }

    @Override
    public String getInventoryResourceFile() {
        return MENU_CONFIG_PATH;
    }

    @Override
    public Path getInventoryConfigFile() {
        return Paths.get(this.plugin.getDataFolder() + File.separator + MENU_CONFIG_PATH);
    }

    @Override
    public String getInventoryId() {
        return MENU_ID;
    }

    @Override
    public InventoryConfigDAO getInventoryConfigDAO() {
        return this.inventoryConfigDAO;
    }
}
