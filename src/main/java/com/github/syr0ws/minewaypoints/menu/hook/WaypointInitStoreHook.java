package com.github.syr0ws.minewaypoints.menu.hook;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.Hook;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;

public class WaypointInitStoreHook implements Hook<CraftVentoryBeforeOpenEvent> {

    public static final String HOOK_ID = "waypoint-init-store";

    @Override
    public void onEvent(CraftVentoryBeforeOpenEvent event) {

        CraftVentory inventory = event.getInventory();
        DataStore store = inventory.getLocalStore();

        // The store may already have data if the inventory is opened due to a forward.
        if (store.hasData(CustomDataStoreKey.WAYPOINT, Waypoint.class)) {
            return;
        }

        // Transferring data from the context to the inventory local store.
        Context context = event.getContext();

        Waypoint waypoint = context.findData(CustomDataStoreKey.WAYPOINT, Waypoint.class)
                .orElseThrow(() -> new NullPointerException("Waypoint not found in context"));

        store.setData(CustomDataStoreKey.WAYPOINT, waypoint, Waypoint.class);
    }
}
