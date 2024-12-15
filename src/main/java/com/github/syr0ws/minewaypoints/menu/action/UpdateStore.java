package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.action.ClickAction;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.inventory.data.DataStoreKey;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;

import java.util.Optional;
import java.util.Set;

public class UpdateStore implements ClickAction {

    public static final String ACTION_NAME = "UPDATE_STORE";

    @Override
    public void execute(CraftVentoryClickEvent event) {

        CraftVentory inventory = event.getInventory();
        Optional<InventoryItem> optional = event.getItem();

        optional.filter(item -> item.getLocalStore().hasData(DataStoreKey.PAGINATION_DATA.getName()))
                .ifPresent(item -> {

                    Waypoint waypoint = item.getLocalStore()
                            .getData(DataStoreKey.PAGINATION_DATA.getName(), Waypoint.class)
                            .orElse(null);

                    System.out.println(waypoint);

                    inventory.getViewer().getViewManager().getSharedStore()
                            .setData(CustomDataStoreKey.WAYPOINT.getName(), Waypoint.class, waypoint);
                });
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
