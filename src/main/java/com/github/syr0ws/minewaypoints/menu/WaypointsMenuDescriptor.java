package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.menu.placeholder.WaypointPlaceholderEnum;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class WaypointsMenuDescriptor implements InventoryDescriptor {

    public static final String MENU_ID = "waypoints-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoints-menu.yml";

    private final Plugin plugin;
    private final InventoryConfigDAO inventoryConfigDAO;

    public WaypointsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {
        this.plugin = plugin;
        this.inventoryConfigDAO = inventoryConfigDAO;
    }

    @Override
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("waypoints-pagination", Waypoint.class, inventory -> {

            WaypointOwner owner = new WaypointOwner(inventory.getViewer().getPlayer());
            owner.addWaypoint(new Waypoint(1, "world", 0, 0, 0, "home", Material.GRASS));

            return owner.getWaypoints();
        }));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        Arrays.stream(WaypointPlaceholderEnum.values())
                .forEach(placeholder -> manager.addPlaceholder(placeholder.get()));
    }

    @Override
    public String getInventoryResourceFile() {
        return MENU_CONFIG_PATH;
    }

    @Override
    public Path getInventoryConfigFile() {
        return Paths.get(this.plugin.getDataFolder() + "/" + MENU_CONFIG_PATH);
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
