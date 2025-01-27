package com.github.syr0ws.minewaypoints.menu.hook;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryBeforeOpenEvent;
import com.github.syr0ws.craftventory.api.inventory.hook.Hook;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;

public class WaypointShareInitStoreHook implements Hook<CraftVentoryBeforeOpenEvent> {

    public static final String HOOK_ID = "waypointshare-init-store";

    @Override
    public void onEvent(CraftVentoryBeforeOpenEvent event) {

        CraftVentory inventory = event.getInventory();
        DataStore store = inventory.getLocalStore();

        // The store may already have data if the inventory is opened due to a forward.
        if(store.hasData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class)) {
            return;
        }

        // Transferring data from the context to the inventory local store.
        Context context = event.getContext();

        WaypointShare share = context.findData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class)
                        .orElseThrow(() -> new NullPointerException("No WaypointShare object found in the context. Check that the inventory is opened by passing it"));

        store.setData(CustomDataStoreKey.WAYPOINT_SHARE, share, WaypointShare.class);
    }
}
