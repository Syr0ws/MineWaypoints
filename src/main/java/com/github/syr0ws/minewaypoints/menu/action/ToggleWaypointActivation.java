package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.service.WaypointActivationService;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.Set;

public class ToggleWaypointActivation extends CommonAction {

    public static final String ACTION_NAME = "TOGGLE_WAYPOINT_ACTIVATION";

    private final Plugin plugin;
    private final WaypointActivationService waypointActivationService;

    public ToggleWaypointActivation(Set<ClickType> clickTypes, Plugin plugin, WaypointActivationService waypointActivationService) {
        super(clickTypes);

        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.plugin = plugin;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        InventoryViewer viewer = event.getViewer();

        InventoryItem item = event.getItem()
                .orElseThrow(() -> new NullPointerException("No item clicked"));

        DataStore store = item.getLocalStore();

        // Only items with a waypoint in their data store (typically paginated waypoint items)
        // can execute this action.
        Waypoint waypoint = store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class)
                .orElseThrow(() -> new IllegalStateException("No waypoint found in item local store"));

        // Activating the waypoint.
        item.disable();

        // TODO Catch errors.
        this.waypointActivationService.activateWaypoint(viewer.getPlayer(), waypoint.getId())
                .complete(item::enable)
                .resolveAsync(this.plugin);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
