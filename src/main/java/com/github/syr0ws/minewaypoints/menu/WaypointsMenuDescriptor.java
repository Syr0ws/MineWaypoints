package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.menu.enhancement.WaypointActivatedDisplay;
import com.github.syr0ws.minewaypoints.menu.placeholder.WaypointPlaceholderEnum;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

            Player player = inventory.getViewer().getPlayer();

            // WaypointUser user = new WaypointUser(player.getUniqueId(), player.getName());
            // user.addWaypoint(new Waypoint(1, user, "name", Material.GRASS, "world", 0d, 0d, 0d));

            return new ArrayList<>();
        }));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        Arrays.stream(WaypointPlaceholderEnum.values())
                .forEach(placeholder -> manager.addPlaceholder(placeholder.get()));
    }

    @Override
    public void addEnhancements(EnhancementManager manager) {
        manager.addEnhancement(DtoNameEnum.PAGINATION_ITEM.name(), new WaypointActivatedDisplay());
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
