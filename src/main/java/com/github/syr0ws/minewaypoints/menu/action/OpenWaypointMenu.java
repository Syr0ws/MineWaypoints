package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewManager;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.exception.InventoryException;
import com.github.syr0ws.craftventory.api.transform.InventoryProvider;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.CraftVentoryLibrary;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.craftventory.common.inventory.data.CommonDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.WaypointDeleteMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;

import java.util.Optional;
import java.util.Set;

public abstract class OpenWaypointMenu extends CommonAction {

    public OpenWaypointMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    protected abstract String getMenuId();

    @Override
    public void execute(CraftVentoryClickEvent event) {

        InventoryViewer viewer = event.getViewer();
        InventoryViewManager viewManager = viewer.getViewManager();

        Optional<Waypoint> optional = this.getWaypoint(event);

        // Waypoint not found.
        if (optional.isEmpty()) {
            return;
        }

        Waypoint waypoint = optional.get();

        // Build the context with the required data.
        Context context = CraftVentoryLibrary.createContext();
        context.addData(CustomDataStoreKey.WAYPOINT, waypoint, Waypoint.class);

        // Open the inventory.
        InventoryService service = event.getInventory().getService();
        InventoryProvider provider = service.getProvider(this.getMenuId())
                .orElseThrow(() -> new InventoryException(String.format("No inventory provider found with id %s", WaypointDeleteMenuDescriptor.MENU_ID)));

        CraftVentory inventory = provider.createInventory(service, viewer.getPlayer());
        viewManager.openView(inventory, false, context);
    }

    private Optional<Waypoint> getWaypoint(CraftVentoryClickEvent event) {

        // Trying to get the waypoint from the item local store if the clicked item is paginated.
        Optional<Waypoint> optional = event.getItem()
                .filter(item -> item.getLocalStore().hasData(CommonDataStoreKey.PAGINATED_DATA, Waypoint.class))
                .flatMap(item -> item.getLocalStore().getData(CommonDataStoreKey.PAGINATED_DATA, Waypoint.class));

        // The item is not paginated.
        if (optional.isPresent()) {
            return optional;
        }

        // Trying to get the waypoint from the inventory local store.
        CraftVentory inventory = event.getInventory();
        DataStore store = inventory.getLocalStore();

        return store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class);
    }
}
