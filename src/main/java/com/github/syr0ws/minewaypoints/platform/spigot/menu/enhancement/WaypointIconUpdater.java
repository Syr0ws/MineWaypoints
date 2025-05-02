package com.github.syr0ws.minewaypoints.platform.spigot.menu.enhancement;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class WaypointIconUpdater extends WaypointEnhancement {

    public static final String ENHANCEMENT_ID = "waypoint-icon-updater";

    private final BukkitWaypointService waypointService;

    public WaypointIconUpdater(BukkitWaypointService waypointService) {
        Validate.notNull(waypointService, "waypointService cannot be null");
        this.waypointService = waypointService;
    }

    @Override
    public void enhance(PaginationItemDto dto, Context context) {

        Optional<Waypoint> optional = super.getWaypoint(context);

        if (optional.isEmpty()) {
            return;
        }

        Waypoint waypoint = optional.get();

        // Updating the displayed item with the waypoint icon.
        ItemStack item = dto.getItem();

        Material icon = Material.getMaterial(waypoint.getIcon());

        // In case the icon is invalid, using the default waypoint icon.
        if (icon == null) {
            icon = this.waypointService.getDefaultWaypointIcon();
        }

        item.setType(icon);
    }

    @Override
    public String getId() {
        return ENHANCEMENT_ID;
    }
}
