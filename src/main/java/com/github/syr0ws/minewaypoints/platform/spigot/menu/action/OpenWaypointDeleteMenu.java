package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.WaypointDeleteMenuDescriptor;

import java.util.Set;

public class OpenWaypointDeleteMenu extends OpenWaypointMenu {

    public static final String ACTION_NAME = "OPEN_WAYPOINT_DELETE_MENU";

    public OpenWaypointDeleteMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getMenuId() {
        return WaypointDeleteMenuDescriptor.MENU_ID;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
