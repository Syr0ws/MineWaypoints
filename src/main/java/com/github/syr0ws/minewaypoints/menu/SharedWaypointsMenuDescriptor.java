package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.menu.enhancement.WaypointActivatedDisplay;
import com.github.syr0ws.minewaypoints.menu.util.PlaceholderUtil;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SharedWaypointsMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "shared-waypoints-menu";
    private static final String MENU_CONFIG_PATH = "menus/shared-waypoints-menu.yml";

    private final WaypointService waypointService;

    public SharedWaypointsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO, WaypointService waypointService) {
        super(plugin, inventoryConfigDAO);
        this.waypointService = waypointService;
    }

    @Override
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("shared-waypoints-pagination", WaypointShare.class, (inventory, pagination) -> {

            Player player = inventory.getViewer().getPlayer();
            UUID playerId = player.getUniqueId();

            List<WaypointShare> list = new ArrayList<>();

            this.waypointService.getSharedWaypoints(playerId)
                    .then(waypointShares -> {
                        pagination.getModel().updateItems(waypointShares);
                        pagination.update(false);
                    })
                    .except(error -> super.getPlugin().getLogger().log(Level.SEVERE, error.getMessage(), error))
                    .resolveAsync(super.getPlugin());

            return list;
        }));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
        PlaceholderUtil.addWaypointSharePlaceholders(manager, this.getPlugin());
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
        return Paths.get(super.getPlugin().getDataFolder() + "/" + MENU_CONFIG_PATH);
    }

    @Override
    public String getInventoryId() {
        return MENU_ID;
    }
}
