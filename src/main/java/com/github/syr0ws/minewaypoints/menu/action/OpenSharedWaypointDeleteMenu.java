package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.menu.SharedWaypointDeleteMenuDescriptor;

import java.util.Set;

public class OpenSharedWaypointDeleteMenu extends OpenWaypointShareMenu {

    public static final String ACTION_NAME = "OPEN_SHARED_WAYPOINT_DELETE_MENU";

    public OpenSharedWaypointDeleteMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getMenuId() {
        return SharedWaypointDeleteMenuDescriptor.MENU_ID;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
