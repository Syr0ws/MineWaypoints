package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewManager;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.exception.InventoryException;
import com.github.syr0ws.craftventory.api.transform.InventoryProvider;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.craftventory.common.inventory.data.CommonDataStoreKey;
import com.github.syr0ws.craftventory.internal.util.SimpleContext;
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

        Optional<Waypoint> optional = event.getItem()
                .filter(item -> item.getLocalStore().hasData(CommonDataStoreKey.PAGINATED_DATA, Waypoint.class))
                .flatMap(item -> item.getLocalStore().getData(CommonDataStoreKey.PAGINATED_DATA, Waypoint.class));

        // The item is not paginated.
        if(optional.isEmpty()) {
            return;
        }

        Waypoint waypoint = optional.get();

        // Build the context with the required data.
        Context context = new SimpleContext();
        context.addData(CustomDataStoreKey.WAYPOINT, waypoint, Waypoint.class);

        // Open the inventory.
        InventoryService service = event.getInventory().getService();
        InventoryProvider provider = service.getProvider(this.getMenuId())
                .orElseThrow(() -> new InventoryException(String.format("No inventory provider found with id %s", WaypointDeleteMenuDescriptor.MENU_ID)));

        CraftVentory inventory = provider.createInventory(service, viewer.getPlayer());
        viewManager.openView(inventory, true);
    }
}
