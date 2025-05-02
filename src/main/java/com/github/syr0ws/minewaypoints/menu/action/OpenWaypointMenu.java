package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;

import java.util.Set;

public abstract class OpenWaypointMenu extends OpenContextualMenu<Waypoint> {

    public OpenWaypointMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getDataKey() {
        return CustomDataStoreKey.WAYPOINT;
    }

    @Override
    protected Class<Waypoint> getDataType() {
        return Waypoint.class;
    }
}
