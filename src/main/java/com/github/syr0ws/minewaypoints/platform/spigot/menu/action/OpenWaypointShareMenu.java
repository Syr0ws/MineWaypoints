package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointShare;

import java.util.Set;

public abstract class OpenWaypointShareMenu extends OpenContextualMenu<WaypointShare> {

    public OpenWaypointShareMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getDataKey() {
        return CustomDataStoreKey.WAYPOINT_SHARE;
    }

    @Override
    protected Class<WaypointShare> getDataType() {
        return WaypointShare.class;
    }
}
