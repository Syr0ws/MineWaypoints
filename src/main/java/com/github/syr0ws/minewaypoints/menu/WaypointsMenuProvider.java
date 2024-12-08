package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.i18n.I18n;
import com.github.syr0ws.craftventory.common.transform.CommonInventoryProvider;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WaypointsMenuProvider extends CommonInventoryProvider {

    public static final String MENU_ID = "waypoints-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoints-menu.yml";

    public WaypointsMenuProvider(I18n i18n, Plugin plugin, InventoryConfigDAO dao) {
        super(i18n, plugin, dao);
    }

    @Override
    public void loadConfig() {
        super.getPlugin().saveResource(MENU_CONFIG_PATH, false);
        super.loadConfig();
    }

    @Override
    protected Path getInventoryConfigFile() {
        return Paths.get(super.getPlugin().getDataFolder() + "/" + MENU_CONFIG_PATH);
    }

    @Override
    protected void addPaginationProviders() {
        WaypointOwner owner = new WaypointOwner(Bukkit.getPlayer("Syrows"));
        owner.addWaypoint(new Waypoint(1, "world", 0, 0, 0, "home", Material.GRASS));
        super.addPaginationProvider("waypoints-pagination", Waypoint.class, owner::getWaypoints);
    }

    @Override
    protected void addEnhancements(EnhancementManager enhancementManager) {

    }

    @Override
    protected void addHooks(HookManager hookManager) {

    }

    @Override
    public String getId() {
        return MENU_ID;
    }
}
