package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.minewaypoints.menu.hook.WaypointInitStoreHook;
import com.github.syr0ws.minewaypoints.menu.hook.WaypointShareInitStoreHook;
import com.github.syr0ws.minewaypoints.menu.util.PlaceholderUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WaypointUnshareMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "waypoint-unshare-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoint-unshare-menu.yml";

    public WaypointUnshareMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {
        super(plugin, inventoryConfigDAO);
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
        PlaceholderUtil.addWaypointSharePlaceholders(manager, this.getPlugin());
    }

    @Override
    public void addHooks(HookManager manager) {
        manager.addHook(WaypointInitStoreHook.HOOK_ID, CraftVentoryBeforeOpenEvent.class, new WaypointShareInitStoreHook());
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
