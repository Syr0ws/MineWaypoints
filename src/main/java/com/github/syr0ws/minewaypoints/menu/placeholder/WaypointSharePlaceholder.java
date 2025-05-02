package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointShare;

public abstract class WaypointSharePlaceholder implements Placeholder {

    @Override
    public boolean accept(Context context) {

        // Case 1: The WaypointShare is stored in the context as a paginated data.
        if (context.hasData(CommonContextKey.PAGINATED_DATA, WaypointShare.class)) {
            return true;
        }

        // Case 2: The WaypointShare is stored in the inventory local data store.
        if (!context.hasData(CommonContextKey.INVENTORY, CraftVentory.class)) {
            return false;
        }

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY, CraftVentory.class);
        DataStore store = inventory.getLocalStore();

        return store.hasData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class);
    }

    protected WaypointShare getWaypointShare(Context context) {

        // Case 1: The WaypointShare is stored in the context as a paginated data.
        if (context.hasData(CommonContextKey.PAGINATED_DATA, WaypointShare.class)) {
            return context.getData(CommonContextKey.PAGINATED_DATA, WaypointShare.class);
        }

        // Case 2: The WaypointShare is stored in the inventory local data store.
        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY, CraftVentory.class);
        DataStore store = inventory.getLocalStore();

        return store.getData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class).get();
    }
}
