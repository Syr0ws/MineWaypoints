package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.common.inventory.data.CommonDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.Material;

import java.util.Set;

public class UpdateWaypointIcon implements ClickAction {

    public static final String ACTION_NAME = "UPDATE_WAYPOINT_ICON";

    @Override
    public void execute(CraftVentoryClickEvent event) {

        CraftVentory inventory = event.getInventory();
        DataStore store = inventory.getLocalStore();

        // Retrieve the waypoint from the inventory local store.
        Waypoint waypoint = store.getData(CustomDataStoreKey.WAYPOINT, Waypoint.class)
                .orElseThrow(() -> new IllegalStateException("Waypoint not found in the local store of the inventory"));

        // Retrieve the Material in paginated data.
        Material material = event.getItem()
                .filter(item -> item.getLocalStore().hasData(CommonDataStoreKey.PAGINATED_DATA, Material.class))
                .flatMap(item -> item.getLocalStore().getData(CommonDataStoreKey.PAGINATED_DATA, Material.class))
                .orElseThrow(() -> new IllegalStateException("Material not found in the local store of the item"));

        // Waypoint icon update.
        waypoint.setIcon(material);
    }

    @Override
    public Set<ClickType> getClickTypes() {
        return Set.of(ClickType.ALL);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
