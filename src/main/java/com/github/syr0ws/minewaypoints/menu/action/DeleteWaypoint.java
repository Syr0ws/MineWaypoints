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
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class DeleteWaypoint extends CommonAction {

    public static final String ACTION_NAME = "DELETE_WAYPOINT";

    private final Plugin plugin;
    private final BukkitWaypointService waypointService;

    public DeleteWaypoint(Set<ClickType> clickTypes, Plugin plugin, BukkitWaypointService waypointService) {
        super(clickTypes);

        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        CraftVentory inventory = event.getInventory();
        Player player = inventory.getViewer().getPlayer();
        DataStore store = inventory.getLocalStore();

        // Retrieve the waypoint from the inventory local store.
        Waypoint waypoint = store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class)
                .orElseThrow(() -> new IllegalArgumentException("Waypoint not found in local store"));

        // Disabling the item to prevent the async task to be executed twice.
        event.getItem().ifPresent(InventoryItem::disable);

        // Delete the waypoint.
        this.waypointService.deleteWaypoint(player, waypoint.getId())
                .then(value -> {

                    // Inventory operations must be executed synchronously.
                    new Promise<>((resolve, reject) -> {
                        InventoryViewer viewer = event.getViewer();
                        InventoryViewManager viewManager = viewer.getViewManager();
                        viewManager.backward(WaypointsMenuDescriptor.MENU_ID); // Go back to the waypoints menu.
                    }).resolveSync(this.plugin);

                })
                .complete(() -> event.getItem().ifPresent(InventoryItem::enable))
                .resolveAsync(this.plugin);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
