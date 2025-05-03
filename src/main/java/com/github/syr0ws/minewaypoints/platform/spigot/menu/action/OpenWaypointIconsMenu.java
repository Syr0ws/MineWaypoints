package com.github.syr0ws.minewaypoints.platform.spigot.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.WaypointIconsMenuDescriptor;

import java.util.Set;

public class OpenWaypointIconsMenu extends OpenWaypointMenu {

    public static final String ACTION_NAME = "OPEN_WAYPOINT_ICONS_MENU";

    public OpenWaypointIconsMenu(Set<ClickType> clickTypes) {
        super(clickTypes);
    }

    @Override
    protected String getMenuId() {
        return WaypointIconsMenuDescriptor.MENU_ID;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
