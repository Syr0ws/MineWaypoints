package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewManager;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.util.Async;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class DeleteWaypoint extends CommonAction {

    public static final String ACTION_NAME = "DELETE_WAYPOINT";

    private final Plugin plugin;
    private final WaypointService waypointService;

    public DeleteWaypoint(Set<ClickType> clickTypes, Plugin plugin, WaypointService waypointService) {
        super(clickTypes);

        if(plugin == null) {
            throw new NullPointerException("plugin cannot be null");
        }

        if(waypointService == null) {
            throw new IllegalArgumentException("waypointService cannot be null");
        }

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        CraftVentory inventory = event.getInventory();
        DataStore store = inventory.getLocalStore();

        Waypoint waypoint = store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class)
                .orElseThrow(() -> new IllegalArgumentException("Waypoint not found in local store"));

        this.waypointService.deleteWaypoint(waypoint.getId())
                .onSuccess(value -> {
                    Async.runSync(this.plugin, () -> {
                        InventoryViewer viewer = event.getViewer();
                        InventoryViewManager viewManager = viewer.getViewManager();
                        viewManager.backward(WaypointsMenuDescriptor.MENU_ID);
                    });
                })
                .onError(error -> {
                    error.printStackTrace();
                })
                .resolveAsync(this.plugin);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
