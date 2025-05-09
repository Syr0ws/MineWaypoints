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
import com.github.syr0ws.minewaypoints.plugin.cache.WaypointOwnerCache;
import com.github.syr0ws.minewaypoints.plugin.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.enhancement.WaypointActivatedDisplay;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.enhancement.WaypointIconUpdater;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.util.PlaceholderUtil;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointOwner;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class WaypointsMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "waypoints-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoints-menu.yml";

    private final BukkitWaypointService waypointService;
    private final BukkitWaypointActivationService waypointActivationService;
    private final WaypointOwnerCache<? extends WaypointOwner> waypointOwnerCache;

    public WaypointsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO, BukkitWaypointService waypointService,
                                   BukkitWaypointActivationService waypointActivationService, WaypointOwnerCache<? extends WaypointOwner> waypointOwnerCache) {
        super(plugin, inventoryConfigDAO);

        Validate.notNull(waypointService, "waypointService cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");
        Validate.notNull(waypointOwnerCache, "waypointOwnerCache cannot be null");

        this.waypointService = waypointService;
        this.waypointActivationService = waypointActivationService;
        this.waypointOwnerCache = waypointOwnerCache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("waypoints-pagination", Waypoint.class, (inventory, pagination) -> {

            Player player = inventory.getViewer().getPlayer();

            new Promise<Object[]>((resolve, reject) -> {

                Object[] values = new Object[2];

                // Retrieving the owner entity associated with the player.
                values[0] = this.waypointOwnerCache.getOwner(player.getUniqueId())
                        .orElseThrow(() -> new WaypointDataException("WaypointOwner not found in cache"));

                // Retrieving the activated waypoints of the player.
                this.waypointActivationService.getActivatedWaypointIds(player.getUniqueId())
                        .then(activatedWaypointIds -> values[1] = activatedWaypointIds)
                        .except(reject)
                        .resolve();

                resolve.accept(values);

            }).then(values -> {

                WaypointOwner owner = (WaypointOwner) values[0];
                Set<Long> waypointIds = (Set<Long>) values[1];

                WaypointActivatedCache cache = new WaypointActivatedCache(waypointIds);

                DataStore store = inventory.getLocalStore();
                store.setData(CustomDataStoreKey.WAYPOINT_ACTIVATED_CACHE, cache, WaypointActivatedCache.class);

                // Paginated waypoints must be initialized in the pagination only when the activated waypoints cache
                // has been created. Otherwise, the enhancement will throw an exception.
                List<Waypoint> waypoints = owner.getWaypoints().stream()
                        .map(waypoint -> (Waypoint) waypoint)
                        // Sorting waypoints by name by default.
                        .sorted(Comparator.comparing(Waypoint::getName))
                        .toList();

                pagination.getModel().updateItems(waypoints);
                pagination.update(false);

            }).resolveAsync(super.getPlugin());

            return new ArrayList<>();
        }));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
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
