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
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.Set;

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
        Waypoint waypoint = this.getWaypoint(event);

        InventoryItem item = event.getItem().get();

        // Activating the waypoint.
        item.disable();

        // TODO Catch errors
        // TODO Update view
        this.waypointActivationService.activateWaypoint(viewer.getPlayer(), waypoint.getId())
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
