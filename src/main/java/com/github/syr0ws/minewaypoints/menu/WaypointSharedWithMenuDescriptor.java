package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.HookManager;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.craftventory.api.transform.provider.ProviderManager;
import com.github.syr0ws.craftventory.common.transform.provider.pagination.PaginationProvider;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.hook.WaypointInitStoreHook;
import com.github.syr0ws.minewaypoints.menu.util.PlaceholderUtil;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WaypointSharedWithMenuDescriptor extends AbstractMenuDescriptor {

    public static final String MENU_ID = "waypoint-shared-with-menu";
    private static final String MENU_CONFIG_PATH = "menus/waypoint-shared-with-menu.yml";

    private final BukkitWaypointService waypointService;

    public WaypointSharedWithMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO, BukkitWaypointService waypointService) {
        super(plugin, inventoryConfigDAO);
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.waypointService = waypointService;
    }

    @Override
    public void addProviders(ProviderManager manager) {

        manager.addProvider(new PaginationProvider<>("waypoint-shared-with-pagination", WaypointShare.class, (inventory, pagination) -> {

            Player player = inventory.getViewer().getPlayer();
            DataStore store = inventory.getLocalStore();

            Waypoint waypoint = store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class)
                    .orElseThrow(() -> new NullPointerException("No waypoint found in inventory local store"));

            List<WaypointShare> list = new ArrayList<>();

            this.waypointService.getSharedWith(player, waypoint.getId())
                    .then(waypointShares -> {
                        waypointShares.sort(Comparator.comparing(share -> share.getSharedWith().getName()));

                        pagination.getModel().updateItems(waypointShares);
                        pagination.update(false);
                    }).resolveAsync(super.getPlugin());

            return list;
        }));
    }

    @Override
    public void addPlaceholders(PlaceholderManager manager) {
        PlaceholderUtil.addWaypointPlaceholders(manager, super.getPlugin());
        PlaceholderUtil.addWaypointSharePlaceholders(manager, this.getPlugin());
    }

    @Override
    public void addHooks(HookManager manager) {
        manager.addHook(WaypointInitStoreHook.HOOK_ID, CraftVentoryBeforeOpenEvent.class, new WaypointInitStoreHook());
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
