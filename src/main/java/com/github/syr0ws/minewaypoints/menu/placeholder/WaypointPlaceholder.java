package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointShare;

public abstract class WaypointPlaceholder implements Placeholder {

    @Override
    public boolean accept(Context context) {

        // Case 1: Waypoint data is stored in the context (pagination).
        if (context.hasData(CommonContextKey.PAGINATED_DATA, Waypoint.class)) {
            return true;
        }

        // Case 2: Waypoint is stored in the WaypointShare stored in the context.
        if (context.hasData(CommonContextKey.PAGINATED_DATA, WaypointShare.class)) {
            return true;
        }

        if (!context.hasData(CommonContextKey.INVENTORY, CraftVentory.class)) {
            return false;
        }

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY, CraftVentory.class);
        DataStore store = inventory.getLocalStore();

        // Case 3: Waypoint data is stored in the local store of the inventory.
        if (store.hasData(CustomDataStoreKey.WAYPOINT, Waypoint.class)) {
            return true;
        }

        // Case 4: Waypoint is stored in the WaypointShare stored in the local store of the inventory.
        return store.hasData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class);
    }

    protected Waypoint getWaypoint(Context context) {

        // Case 1: Waypoint can be provided from the context.
        if (context.hasData(CommonContextKey.PAGINATED_DATA, Waypoint.class)) {
            return context.getData(CommonContextKey.PAGINATED_DATA, Waypoint.class);
        }

        // Case 2: Waypoint can be provided from the WaypointShare stored in the context. This is the
        // case for the menu that lists the shared waypoints.
        if (context.hasData(CommonContextKey.PAGINATED_DATA, WaypointShare.class)) {
            return context.getData(CommonContextKey.PAGINATED_DATA, WaypointShare.class).getWaypoint();
        }

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY, CraftVentory.class);
        DataStore store = inventory.getLocalStore();

        // Case 3: Waypoint can be provided from the local store of the inventory.
        if (store.hasData(CustomDataStoreKey.WAYPOINT, Waypoint.class)) {
            return store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class).get();
        }

        // Case 4: Waypoint can be provided from WaypointShare stored in the local store of the inventory.
        return store.getData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class)
                .map(WaypointShare::getWaypoint)
                .get();
    }
}
