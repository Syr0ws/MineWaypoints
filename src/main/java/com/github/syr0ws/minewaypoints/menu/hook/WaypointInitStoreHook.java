package com.github.syr0ws.minewaypoints.menu.hook;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
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
        Context context = event.getContext();

        Waypoint waypoint = context.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class);

        inventory.getLocalStore().setData(CustomDataStoreKey.WAYPOINT, Waypoint.class, waypoint);
    }
}
