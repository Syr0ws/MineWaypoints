package com.github.syr0ws.minewaypoints.platform.spigot.menu.util;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;

import java.util.Optional;

public class DataUtil {

    public static <T> Optional<T> getContextualData(CraftVentoryClickEvent event, String dataKey, Class<T> dataType) {

        // Case 1: Trying to get the data from the item local store if the clicked item is paginated.
        Optional<T> optional = event.getItem()
                .filter(item -> item.getLocalStore().hasData(CommonContextKey.PAGINATED_DATA, dataType))
                .flatMap(item -> item.getLocalStore().getData(CommonContextKey.PAGINATED_DATA, dataType));

        if (optional.isPresent()) {
            return optional;
        }

        // Case 2: Trying to get the data from the inventory local store.
        CraftVentory inventory = event.getInventory();
        DataStore inventoryStore = inventory.getLocalStore();

        if (inventoryStore.hasData(dataKey, dataType)) {
            return inventoryStore.getData(dataKey, dataType);
        }

        // Case 3: Trying to get the data from the item local store.
        Optional<InventoryItem> itemOptional = event.getItem();

        if (itemOptional.isEmpty()) {
            return Optional.empty();
        }

        InventoryItem item = itemOptional.get();
        DataStore itemStore = item.getLocalStore();

        return itemStore.getData(dataKey, dataType);
    }
}
