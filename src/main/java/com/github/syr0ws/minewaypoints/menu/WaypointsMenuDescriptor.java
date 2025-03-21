package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.cache.WaypointActivatedCache;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.enhancement.WaypointActivatedDisplay;
import com.github.syr0ws.minewaypoints.menu.enhancement.WaypointIconUpdater;
import com.github.syr0ws.minewaypoints.menu.util.PlaceholderUtil;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointActivationService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class WaypointsMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "waypoints-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoints-menu.yml";

    private final WaypointUserCache<? extends WaypointOwner> waypointUserCache;
    private final BukkitWaypointActivationService waypointActivationService;

    public WaypointsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO, WaypointUserCache<? extends WaypointOwner> waypointUserCache, BukkitWaypointActivationService waypointActivationService) {
        super(plugin, inventoryConfigDAO);

        Validate.notNull(waypointUserCache, "waypointUserCache cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.waypointUserCache = waypointUserCache;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("waypoints-pagination", Waypoint.class, (inventory, pagination) -> {

            Player player = inventory.getViewer().getPlayer();

            WaypointOwner user = this.waypointUserCache.getUser(player.getUniqueId())
                    .orElseThrow(() -> new NullPointerException("User not found"));

            this.waypointActivationService.getActivatedWaypointIds(player.getUniqueId())
                    .then(waypointIds -> {

                        WaypointActivatedCache cache = new WaypointActivatedCache(waypointIds);

                        DataStore store = inventory.getLocalStore();
                        store.setData(CustomDataStoreKey.WAYPOINT_ACTIVATED_CACHE, cache, WaypointActivatedCache.class);

                        // Paginated waypoints must be initialized in the pagination only when the activated waypoints cache
                        // has been created. Otherwise, the enhancement will throw an exception.
                        List<Waypoint> waypoints = user.getWaypoints().stream()
                                .map(waypoint -> (Waypoint) waypoint)
                                // Sorting waypoints by name by default.
                                .sorted(Comparator.comparing(Waypoint::getName))
                                .toList();

                        pagination.getModel().updateItems(waypoints);
                        pagination.update(false);
                    })
                    .except(throwable -> {
                        String message = String.format("An error occurred while initializing activated waypoints cache for player %s", player.getUniqueId());
                        super.getPlugin().getLogger().log(Level.SEVERE, message, throwable);
                    })
                    .resolveAsync(super.getPlugin());

            return new ArrayList<>();
        }));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
    }

    @Override
    public void addEnhancements(EnhancementManager manager) {
        manager.addEnhancement(DtoNameEnum.PAGINATION_ITEM.name(), new WaypointIconUpdater());
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
