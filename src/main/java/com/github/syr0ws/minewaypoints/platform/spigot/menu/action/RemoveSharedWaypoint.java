package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

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
import com.github.syr0ws.minewaypoints.platform.spigot.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointShare;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class RemoveSharedWaypoint extends CommonAction {

    public static final String ACTION_NAME = "REMOVE_SHARED_WAYPOINT";

    private final Plugin plugin;
    private final BukkitWaypointService waypointService;

    public RemoveSharedWaypoint(Set<ClickType> clickTypes, Plugin plugin, BukkitWaypointService waypointService) {
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
        WaypointShare share = store.getData(CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class)
                .orElseThrow(() -> new IllegalArgumentException("WaypointShare not found in local store"));

        Waypoint waypoint = share.getWaypoint();

        // Disabling the item to prevent the async task to be executed twice.
        event.getItem().ifPresent(InventoryItem::disable);

        // Unshare the waypoint.
        this.waypointService.unshareWaypointBySharedWith(player, waypoint.getId())
                .then(value -> {

                    // Inventory operations must be executed synchronously.
                    new Promise<>((resolve, reject) -> {
                        InventoryViewer viewer = event.getViewer();
                        InventoryViewManager viewManager = viewer.getViewManager();
                        viewManager.backward(); // Go back to the previous menu.
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
