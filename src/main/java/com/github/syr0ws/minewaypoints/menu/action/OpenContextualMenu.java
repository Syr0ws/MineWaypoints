package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewManager;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.exception.InventoryException;
import com.github.syr0ws.craftventory.api.transform.InventoryProvider;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.CraftVentoryLibrary;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;

import java.util.Optional;
import java.util.Set;

public abstract class OpenContextualMenu<T> extends CommonAction {

    public OpenContextualMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    protected abstract String getDataKey();

    protected abstract Class<T> getDataType();

    protected abstract String getMenuId();

    @Override
    public void execute(CraftVentoryClickEvent event) {

        InventoryViewer viewer = event.getViewer();
        InventoryViewManager viewManager = viewer.getViewManager();

        Optional<T> optional = this.getContextualData(event);

        // Waypoint not found.
        if (optional.isEmpty()) {
            return;
        }

        T data = optional.get();

        String dataKey = this.getDataKey();
        Class<T> dataType = this.getDataType();

        // Build the context with the required data.
        Context context = CraftVentoryLibrary.createContext();
        context.addData(dataKey, data, dataType);

        // Open the inventory.
        String targetMenuId = this.getMenuId();

        InventoryService service = event.getInventory().getService();
        InventoryProvider provider = service.getProvider(targetMenuId)
                .orElseThrow(() -> new InventoryException(String.format("No inventory provider found for inventory '%s'", targetMenuId)));

        CraftVentory inventory = provider.createInventory(service, viewer.getPlayer());
        viewManager.openView(inventory, false, context);
    }

    private Optional<T> getContextualData(CraftVentoryClickEvent event) {

        String dataKey = this.getDataKey();
        Class<T> dataType = this.getDataType();

        // Trying to get the waypoint from the item local store if the clicked item is paginated.
        Optional<T> optional = event.getItem()
                .filter(item -> item.getLocalStore().hasData(dataKey, dataType))
                .flatMap(item -> item.getLocalStore().getData(dataKey, dataType));

        // The item is not paginated.
        if (optional.isPresent()) {
            return optional;
        }

        // Trying to get the waypoint from the inventory local store.
        CraftVentory inventory = event.getInventory();
        DataStore store = inventory.getLocalStore();

        return store.getData(dataKey, dataType);
    }
}
