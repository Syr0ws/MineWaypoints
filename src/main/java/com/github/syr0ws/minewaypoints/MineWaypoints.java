package com.github.syr0ws.minewaypoints;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.config.action.ClickActionLoaderFactory;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.common.CraftVentoryLibrary;
import com.github.syr0ws.minewaypoints.command.CommandWaypoints;
import com.github.syr0ws.minewaypoints.menu.WaypointIconsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class MineWaypoints extends JavaPlugin {

    private InventoryService inventoryService;

    @Override
    public void onEnable() {

        this.loadConfiguration();

        this.registerInventoryProviders();
        this.registerCommands();
    }

    private void loadConfiguration() {
        super.saveDefaultConfig();
    }

    private void registerCommands() {
        super.getCommand("waypoints").setExecutor(new CommandWaypoints(this, inventoryService));
    }

    private void registerInventoryProviders() {

        this.inventoryService = CraftVentoryLibrary.createInventoryService(this);

        ClickActionLoaderFactory<ConfigurationSection> factory =
                CraftVentoryLibrary.createDefaultClickActionLoaderFactory();

        InventoryConfigDAO dao = CraftVentoryLibrary.createDefaultConfigDAO(factory);

        this.inventoryService.createProvider(new WaypointsMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointIconsMenuDescriptor(this, dao));
        this.inventoryService.loadInventoryConfigs();
    }
}
