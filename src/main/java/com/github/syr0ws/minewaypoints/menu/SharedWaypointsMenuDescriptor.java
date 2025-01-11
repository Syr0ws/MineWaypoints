package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.menu.enhancement.WaypointActivatedDisplay;
import com.github.syr0ws.minewaypoints.menu.placeholder.WaypointPlaceholderEnum;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SharedWaypointsMenuDescriptor implements InventoryDescriptor {

    public static final String MENU_ID = "shared-waypoints-menu";
    private static final String MENU_CONFIG_PATH = "menus/shared-waypoints-menu.yml";

    private final Plugin plugin;
    private final InventoryConfigDAO inventoryConfigDAO;
    private final WaypointService waypointService;

    public SharedWaypointsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO, WaypointService waypointService) {
        this.plugin = plugin;
        this.inventoryConfigDAO = inventoryConfigDAO;
        this.waypointService = waypointService;
    }

    @Override
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("shared-waypoints-pagination", WaypointShare.class, inventory -> {

            Player player = inventory.getViewer().getPlayer();
            UUID playerId = player.getUniqueId();

            List<WaypointShare> list = new ArrayList<>();

            this.waypointService.getSharedWaypoints(playerId)
                    .then(list::addAll)
                    .except(error -> this.plugin.getLogger().log(Level.SEVERE, error.getMessage(), error));

            return list;
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