package com.github.syr0ws.minewaypoints.menu.enhancement;

import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class WaypointIconUpdater extends WaypointEnhancement {

    public static final String ENHANCEMENT_ID = "waypoint-icon-updater";

    @Override
    public void enhance(PaginationItemDto dto, Context context) {

        Optional<Waypoint> optional = super.getWaypoint(context);

        if(optional.isEmpty()) {
            return;
        }

        Waypoint waypoint = optional.get();

        // Updating the displayed item with the waypoint icon.
        ItemStack item = dto.getItem();
        item.setType(waypoint.getIcon());
    }

    @Override
    public String getId() {
        return ENHANCEMENT_ID;
    }
}
