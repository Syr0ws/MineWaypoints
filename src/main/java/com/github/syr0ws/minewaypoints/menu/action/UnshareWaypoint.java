package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewManager;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.logging.Level;

public class UnshareWaypoint extends CommonAction {

    public static final String ACTION_NAME = "UNSHARE_WAYPOINT";

    private final Plugin plugin;
    private final WaypointService waypointService;

    public UnshareWaypoint(Set<ClickType> clickTypes, Plugin plugin, WaypointService waypointService) {
        super(clickTypes);

        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        CraftVentory inventory = event.getInventory();
        DataStore store = inventory.getLocalStore();

        // Retrieve the waypoint from the inventory local store.
        WaypointShare share = store.getData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class)
                .orElseThrow(() -> new IllegalArgumentException("WaypointShare not found in local store"));

        WaypointUser sharedWith = share.getSharedWith();
        Waypoint waypoint = share.getWaypoint();

        // Disabling the item to prevent the async task to be executed twice.
        event.getItem().ifPresent(InventoryItem::disable);

        // Unshare the waypoint.
        this.waypointService.unshareWaypoint(sharedWith.getName(), waypoint.getId())
                .then(value -> new Promise<>((resolve, reject) -> {
                    InventoryViewer viewer = event.getViewer();
                    InventoryViewManager viewManager = viewer.getViewManager();
                    viewManager.backward();
                }).resolveSync(this.plugin))
                .except(error ->
                        this.plugin.getLogger().log(Level.SEVERE, "An error occurred while unsharing the waypoint", error))
                .complete(() ->
                        event.getItem().ifPresent(InventoryItem::enable))
                .resolveAsync(this.plugin);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
