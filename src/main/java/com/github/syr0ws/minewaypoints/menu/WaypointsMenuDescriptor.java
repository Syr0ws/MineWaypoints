package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.transform.enhancement.EnhancementManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.dto.DtoNameEnum;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.cache.WaypointActivatedCache;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.enhancement.WaypointActivatedDisplay;
import com.github.syr0ws.minewaypoints.menu.enhancement.WaypointIconUpdater;
import com.github.syr0ws.minewaypoints.menu.util.PlaceholderUtil;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointUserService;
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

    private final BukkitWaypointUserService waypointUserService;
    private final BukkitWaypointActivationService waypointActivationService;

    public WaypointsMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO, BukkitWaypointUserService waypointUserService, BukkitWaypointActivationService waypointActivationService) {
        super(plugin, inventoryConfigDAO);

        Validate.notNull(waypointUserService, "waypointUserService cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.waypointUserService = waypointUserService;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("waypoints-pagination", Waypoint.class, (inventory, pagination) -> {

            Player player = inventory.getViewer().getPlayer();

            new Promise<Object[]>((resolve, reject) -> {

                Object[] values = new Object[2];

                this.waypointUserService.getWaypointOwner(player)
                        .then(optional -> optional.ifPresent(owner -> values[0] = owner))
                        .resolve();

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
