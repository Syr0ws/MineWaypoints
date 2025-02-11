package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.menu.WaypointUnshareMenuDescriptor;

import java.util.Set;

public class OpenWaypointUnshareMenu extends OpenWaypointShareMenu {

    public static final String ACTION_NAME = "OPEN_WAYPOINT_UNSHARE_MENU";

    public OpenWaypointUnshareMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getMenuId() {
        return WaypointUnshareMenuDescriptor.MENU_ID;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
