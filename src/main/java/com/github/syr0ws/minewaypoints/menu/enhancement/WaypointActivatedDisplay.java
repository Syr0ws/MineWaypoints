package com.github.syr0ws.minewaypoints.menu.enhancement;

import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.exception.InventoryException;
import com.github.syr0ws.craftventory.api.transform.enhancement.Enhancement;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.cache.WaypointActivatedCache;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class WaypointActivatedDisplay extends WaypointEnhancement {

    public static final String ENHANCEMENT_ID = "waypoint-activated-display";

    @Override
    public void enhance(PaginationItemDto dto, Context context) {

        Optional<Waypoint> optional = super.getWaypoint(context);

        if(optional.isEmpty()) {
            return;
        }

        Waypoint waypoint = optional.get();

        CraftVentory inventory = context.getData(CommonContextKey.INVENTORY, CraftVentory.class);
        DataStore store = inventory.getLocalStore();

        WaypointActivatedCache cache = store.getData(CustomDataStoreKey.WAYPOINT_ACTIVATED_CACHE, WaypointActivatedCache.class)
                .orElseThrow(() -> new InventoryException("WaypointActivatedCache not found in inventory's local store"));

        if(!cache.isActivated(waypoint.getId())) {
            return;
        }

        ItemStack item = dto.getItem();
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            throw new InventoryException("ItemMeta is null");
        }

        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
    }

    @Override
    public String getId() {
        return ENHANCEMENT_ID;
    }
}
