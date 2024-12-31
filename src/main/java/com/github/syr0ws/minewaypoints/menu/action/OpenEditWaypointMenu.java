package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.menu.WaypointEditMenuDescriptor;

import java.util.Set;

public class OpenEditWaypointMenu extends OpenWaypointMenu {

    public static final String ACTION_NAME = "OPEN_WAYPOINT_EDIT_MENU";

    public OpenEditWaypointMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getMenuId() {
        return WaypointEditMenuDescriptor.MENU_ID;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
