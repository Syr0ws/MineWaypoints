package com.github.syr0ws.minewaypoints.platform.spigot.menu;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.platform.spigot.cache.WaypointActivatedCache;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.enhancement.WaypointActivatedDisplay;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.enhancement.WaypointIconUpdater;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.util.PlaceholderUtil;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointShare;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

public class SharedWaypointsMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "shared-waypoints-menu";
    private static final String MENU_CONFIG_PATH = "menus/shared-waypoints-menu.yml";

    private final BukkitWaypointService waypointService;
    private final BukkitWaypointActivationService waypointActivationService;

    public SharedWaypointsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO, BukkitWaypointService waypointService, BukkitWaypointActivationService waypointActivationService) {
        super(plugin, inventoryConfigDAO);

        Validate.notNull(waypointService, "waypointService cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.waypointService = waypointService;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("shared-waypoints-pagination", WaypointShare.class, (inventory, pagination) -> {

            Player player = inventory.getViewer().getPlayer();
            UUID playerId = player.getUniqueId();

            new Promise<Object[]>((resolve, reject) -> {

                Object[] values = new Object[2];

                this.waypointService.getSharedWaypoints(player)
                        .then(waypointShares -> values[0] = waypointShares)
                        .except(reject)
                        .resolve();

                this.waypointActivationService.getActivatedWaypointIds(player.getUniqueId())
                        .then(activatedWaypointIds -> values[1] = activatedWaypointIds)
                        .except(reject)
                        .resolve();

                resolve.accept(values);
            })
                    .then(values -> {

                        List<WaypointShare> waypointShares = (List<WaypointShare>) values[0];
                        waypointShares.sort(Comparator.comparing(share -> share.getWaypoint().getName()));

                        Set<Long> waypointIds = (Set<Long>) values[1];

                        WaypointActivatedCache cache = new WaypointActivatedCache(waypointIds);

                        DataStore store = inventory.getLocalStore();
                        store.setData(CustomDataStoreKey.WAYPOINT_ACTIVATED_CACHE, cache, WaypointActivatedCache.class);

                        pagination.getModel().updateItems(waypointShares);
                        pagination.update(false);
                    })
                    .except(throwable -> {
                        String message = String.format("An error occurred while retrieving shared waypoints for %s", playerId);
                        super.getPlugin().getLogger().log(Level.SEVERE, message, throwable);
                    })
                    .resolveAsync(super.getPlugin());

            return new ArrayList<>();
        }));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
        PlaceholderUtil.addWaypointSharePlaceholders(manager, this.getPlugin());
    }

    @Override
    public void addEnhancements(EnhancementManager manager) {
        manager.addEnhancement(DtoNameEnum.PAGINATION_ITEM.name(), new WaypointIconUpdater(this.waypointService));
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
