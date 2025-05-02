package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.WaypointSharedWithMenuDescriptor;

import java.util.Set;

public class OpenWaypointSharedWithMenu extends OpenWaypointMenu {

    public static final String ACTION_NAME = "OPEN_WAYPOINT_SHARED_WITH_MENU";

    public OpenWaypointSharedWithMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getMenuId() {
        return WaypointSharedWithMenuDescriptor.MENU_ID;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
