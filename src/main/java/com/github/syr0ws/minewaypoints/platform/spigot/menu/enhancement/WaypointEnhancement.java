package com.github.syr0ws.minewaypoints.platform.spigot.menu.enhancement;

import com.github.syr0ws.craftventory.api.transform.enhancement.Enhancement;
import com.github.syr0ws.craftventory.api.util.Context;
import com.github.syr0ws.craftventory.common.transform.dto.pagination.PaginationItemDto;
import com.github.syr0ws.craftventory.common.util.CommonContextKey;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointShare;

import java.util.Optional;

public abstract class WaypointEnhancement implements Enhancement<PaginationItemDto> {

    protected Optional<Waypoint> getWaypoint(Context context) {

        if (context.hasData(CommonContextKey.PAGINATED_DATA, Waypoint.class)) {
            Waypoint waypoint = context.getData(CommonContextKey.PAGINATED_DATA, Waypoint.class);
            return Optional.of(waypoint);
        }

        if (context.hasData(CommonContextKey.PAGINATED_DATA, WaypointShare.class)) {
            WaypointShare share = context.getData(CommonContextKey.PAGINATED_DATA, WaypointShare.class);
            return Optional.of(share.getWaypoint());
        }

        return Optional.empty();
    }

    @Override
    public Class<PaginationItemDto> getDTOClass() {
        return PaginationItemDto.class;
    }
}
