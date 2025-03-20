package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewManager;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.inventory.data.CommonDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class UpdateWaypointIcon implements ClickAction {

    public static final String ACTION_NAME = "UPDATE_WAYPOINT_ICON";

    private final Plugin plugin;
    private final BukkitWaypointService waypointService;

    public UpdateWaypointIcon(Plugin plugin, BukkitWaypointService waypointService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        CraftVentory inventory = event.getInventory();
        Player player = inventory.getViewer().getPlayer();
        DataStore store = inventory.getLocalStore();

        // Retrieve the waypoint from the inventory local store.
        Waypoint waypoint = store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class)
                .orElseThrow(() -> new IllegalStateException("Waypoint not found in the local store of the inventory"));

        // Retrieve the Material in paginated data.
        Material material = event.getItem()
                .filter(item -> item.getLocalStore().hasData(CommonDataStoreKey.PAGINATED_DATA, Material.class))
                .flatMap(item -> item.getLocalStore().getData(CommonDataStoreKey.PAGINATED_DATA, Material.class))
                .orElseThrow(() -> new IllegalStateException("Material not found in the local store of the item"));

        // Disabling the item to prevent the async task to be executed twice.
        event.getItem().ifPresent(InventoryItem::disable);

        // Update the icon of the waypoint.
        this.waypointService.updateWaypointIconById(player, waypoint.getId(), material)
                .then(updated -> {

                    // Inventory operations must be executed synchronously.
                    new Promise<>((resolve, reject) -> {
                        InventoryViewer viewer = event.getViewer();
                        InventoryViewManager viewManager = viewer.getViewManager();
                        viewManager.backward(); // Go back to the waypoints menu.
                    }).resolveSync(this.plugin);

                }).complete(() -> event.getItem().ifPresent(InventoryItem::enable))
                .resolveAsync(this.plugin);
    }

    @Override
    public Set<ClickType> getClickTypes() {
        return Set.of(ClickType.ALL);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
