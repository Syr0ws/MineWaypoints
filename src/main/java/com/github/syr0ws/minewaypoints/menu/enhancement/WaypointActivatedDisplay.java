package com.github.syr0ws.minewaypoints.menu.enhancement;

import com.github.syr0ws.craftventory.api.transform.enhancement.Enhancement;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WaypointActivatedDisplay implements Enhancement<PaginationItemDto> {

    public static final String ENHANCEMENT_ID = "waypoint-activated-display";

    @Override
    public void enhance(PaginationItemDto dto, Context context) {

        if(!context.hasData(CommonContextKey.PAGINATION_ITEM.name())) {
            return;
        }

        Waypoint waypoint = context.getData(CommonContextKey.PAGINATION_ITEM.name(), Waypoint.class);

        if(!waypoint.isActivated()) {
            return;
        }

        ItemStack item = dto.getItem();

        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
    }

    @Override
    public Class<PaginationItemDto> getDTOClass() {
        return PaginationItemDto.class;
    }

    @Override
    public String getId() {
        return ENHANCEMENT_ID;
    }
}
