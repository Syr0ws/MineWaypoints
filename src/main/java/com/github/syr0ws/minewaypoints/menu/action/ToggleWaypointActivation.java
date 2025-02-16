package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.craftventory.common.inventory.data.CommonDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.service.WaypointActivationService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.logging.Level;

public class ToggleWaypointActivation extends CommonAction {

    public static final String ACTION_NAME = "TOGGLE_WAYPOINT_ACTIVATION";

    private final Plugin plugin;
    private final WaypointActivationService waypointActivationService;

    public ToggleWaypointActivation(Set<ClickType> clickTypes, Plugin plugin, WaypointActivationService waypointActivationService) {
        super(clickTypes);

        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.plugin = plugin;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        InventoryViewer viewer = event.getViewer();
        Player player = viewer.getPlayer();
        Waypoint waypoint = this.getWaypoint(event);
        InventoryItem item = event.getItem().get();

        // Activating the waypoint.
        item.disable();

        this.waypointActivationService.toggleWaypoint(player, waypoint.getId())
                .then(status -> {
                    // TODO
                    System.out.println(status);
                })
                .except(throwable -> {
                    String message = String.format("An error occurred while toggling waypoint activation for player %s", player.getUniqueId());
                    this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                })
                .complete(item::enable)
                .resolveAsync(this.plugin);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    private Waypoint getWaypoint(CraftVentoryClickEvent event) {

        InventoryItem item = event.getItem()
                .orElseThrow(() -> new NullPointerException("No item clicked"));

        DataStore itemStore = item.getLocalStore();

        // Case 1: Waypoint is stored directly in the item local store.
        if(itemStore.hasData(CustomDataStoreKey.WAYPOINT, Waypoint.class)) {
            return itemStore.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class).get();
        }

        // Case 2: Waypoint is stored as a paginated data.
        if(itemStore.hasData(CommonDataStoreKey.PAGINATED_DATA, Waypoint.class)) {
            return itemStore.getData(CommonDataStoreKey.PAGINATED_DATA, Waypoint.class).get();
        }

        // Case 3: Waypoint is stored in the inventory local store.
        CraftVentory inventory = event.getInventory();
        DataStore inventoryStore = inventory.getLocalStore();

        if(inventoryStore.hasData(CustomDataStoreKey.WAYPOINT, Waypoint.class)) {
            return inventoryStore.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class).get();
        }

        throw new IllegalStateException("No waypoint found");
    }
}
