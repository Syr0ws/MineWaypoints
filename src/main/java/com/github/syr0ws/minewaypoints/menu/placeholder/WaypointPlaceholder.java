package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewManager;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;

public abstract class WaypointPlaceholder implements Placeholder {

    @Override
    public boolean accept(Context context) {

        // Case 1: Waypoint data is stored in the context (pagination).
        if(!context.hasData(CommonContextKey.PAGINATION_ITEM.name())) {
            return false;
        }

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY.name(), CraftVentory.class);

        // Case 2: Waypoint data is stored in the local store.
        if(!inventory.getLocalStore().hasData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class)) {
            return false;
        }

        // Case 3: Waypoint data is stored in the shared store.
        InventoryViewer viewer = inventory.getViewer();
        InventoryViewManager viewManager = viewer.getViewManager();

        return viewManager.getSharedStore().hasData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class);
    }

    protected Waypoint getWaypoint(Context context) {

        // Case 1: Waypoint data is stored in the context (pagination).
        if(context.hasData(CommonContextKey.PAGINATION_ITEM.name())) {
            return context.getData(CommonContextKey.PAGINATION_ITEM.name(), Waypoint.class);
        }

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY.name(), CraftVentory.class);

        // Case 2: Waypoint data is stored in the local store.
        if(inventory.getLocalStore().hasData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class)) {
            return inventory.getLocalStore().getData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class).orElse(null);
        }

        // Case 3: Waypoint data is stored in the shared store.
        InventoryViewer viewer = inventory.getViewer();
        InventoryViewManager viewManager = viewer.getViewManager();

        return viewManager.getSharedStore().getData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class).orElse(null);
    }
}
