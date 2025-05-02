package com.github.syr0ws.minewaypoints.platform.spigot.menu;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.api.transform.InventoryDescriptor;
import org.bukkit.plugin.Plugin;

public abstract class AbstractMenuDescriptor implements InventoryDescriptor {

    private final Plugin plugin;
    private final InventoryConfigDAO inventoryConfigDAO;

    public AbstractMenuDescriptor(Plugin plugin, InventoryConfigDAO inventoryConfigDAO) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(inventoryConfigDAO, "inventoryConfigDAO cannot be null");

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
