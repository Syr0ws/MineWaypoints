package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.placeholder.WaypointNamePlaceholder;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        manager.addPlaceholder(new WaypointNamePlaceholder());
    }

    @Override
    public void addHooks(HookManager manager) {

        manager.addHook("init-store", CraftVentoryBeforeOpenEvent.class, event -> {

            CraftVentory inventory = event.getInventory();
            Context context = event.getContext();

            Waypoint waypoint = context.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class);

            inventory.getLocalStore().setData(CustomDataStoreKey.WAYPOINT, Waypoint.class, waypoint);
        });
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
