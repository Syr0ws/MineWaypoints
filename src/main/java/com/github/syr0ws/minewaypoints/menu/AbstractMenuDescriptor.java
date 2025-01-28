package com.github.syr0ws.minewaypoints.menu;

import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import org.bukkit.plugin.Plugin;

public abstract class AbstractMenuDescriptor implements InventoryDescriptor {

    private final Plugin plugin;
    private final InventoryConfigDAO inventoryConfigDAO;

    public AbstractMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if (inventoryConfigDAO == null) {
            throw new IllegalArgumentException("inventoryConfigDAO cannot be null");
        }

        this.plugin = plugin;
        this.inventoryConfigDAO = inventoryConfigDAO;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public InventoryConfigDAO getInventoryConfigDAO() {
        return this.inventoryConfigDAO;
    }
}
