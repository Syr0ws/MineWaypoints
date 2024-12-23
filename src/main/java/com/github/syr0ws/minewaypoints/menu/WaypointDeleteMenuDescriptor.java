package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;

import java.nio.file.Path;

public class WaypointDeleteMenuDescriptor implements InventoryDescriptor {

    public static final String MENU_ID = "waypoint-delete-menu";

    @Override
    public String getInventoryResourceFile() {
        return "";
    }

    @Override
    public Path getInventoryConfigFile() {
        return null;
    }

    @Override
    public String getInventoryId() {
        return "";
    }

    @Override
    public InventoryConfigDAO getInventoryConfigDAO() {
        return null;
    }
}
