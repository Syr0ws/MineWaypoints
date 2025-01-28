package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.minewaypoints.menu.SharedWaypointDeleteMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.model.WaypointShare;

import java.util.Set;

public class OpenSharedWaypointDeleteMenu extends OpenContextualMenu<WaypointShare> {

    public static final String ACTION_NAME = "OPEN_SHAREDèWAYPOINT_DELETE_MENU";

    public OpenSharedWaypointDeleteMenu(Set<ClickType> clickTypes) {
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

    @Override
    protected String getMenuId() {
        return SharedWaypointDeleteMenuDescriptor.MENU_ID;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }
}
