package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;

public abstract class WaypointPlaceholder implements Placeholder {

    @Override
    public boolean accept(Context context) {

        // Case 1: Waypoint data is stored in the context (pagination).
        if (context.hasData(CommonContextKey.PAGINATED_DATA, Waypoint.class)) {
            return true;
        }

        // Case 2: Waypoint data is stored in the local store.
        if(!context.hasData(CommonContextKey.INVENTORY, CraftVentory.class)) {
            return false;
        }

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY, CraftVentory.class);

        return inventory.getLocalStore().hasData(CustomDataStoreKey.WAYPOINT, Waypoint.class);
    }

    protected Waypoint getWaypoint(Context context) {

        if (context.hasData(CommonContextKey.PAGINATED_DATA, Waypoint.class)) {
            return context.getData(CommonContextKey.PAGINATED_DATA, Waypoint.class);
        }

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY, CraftVentory.class);

        return inventory.getLocalStore().getData(CustomDataStoreKey.WAYPOINT, Waypoint.class).orElse(null);
    }
}
