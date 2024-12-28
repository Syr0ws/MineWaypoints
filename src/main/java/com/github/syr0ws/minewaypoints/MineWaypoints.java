package com.github.syr0ws.minewaypoints;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.config.action.ClickActionLoaderFactory;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.common.CraftVentoryLibrary;
import com.github.syr0ws.minewaypoints.command.CommandWaypoints;
import com.github.syr0ws.minewaypoints.database.DatabaseConnectionLoader;
import com.github.syr0ws.minewaypoints.exception.ConfigurationException;
import com.github.syr0ws.minewaypoints.menu.WaypointDeleteMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.WaypointIconsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.action.OpenDeleteWaypointMenuLoader;
import com.github.syr0ws.minewaypoints.menu.action.OpenWaypointIconsMenuLoader;
import com.github.syr0ws.minewaypoints.menu.action.UpdateWaypointIconLoader;
import io.ebean.Database;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MineWaypoints extends JavaPlugin {

    private InventoryService inventoryService;

    private Database database;

    @Override
    public void onEnable() {

        this.loadConfiguration();

        try {
            this.loadDatabase();
        } catch (Exception exception) {
            this.getLogger().log(Level.SEVERE, "An error occurred while creating the database connection", exception);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.registerInventoryProviders();
        this.registerCommands();
    }

    private void loadConfiguration() {
        super.saveDefaultConfig();
    }

    private void loadDatabase() throws ConfigurationException {
        DatabaseConnectionLoader loader = new DatabaseConnectionLoader(this);
        this.database = loader.loadConnection();
    }

    private void registerCommands() {
        super.getCommand("waypoints").setExecutor(new CommandWaypoints(this, inventoryService));
    }

    private void registerInventoryProviders() {

        this.inventoryService = CraftVentoryLibrary.createInventoryService(this);

        // Register action loaders.
        ClickActionLoaderFactory<ConfigurationSection> factory =
                CraftVentoryLibrary.createDefaultClickActionLoaderFactory();

        factory.addLoader(new OpenWaypointIconsMenuLoader());
        factory.addLoader(new OpenDeleteWaypointMenuLoader());
        factory.addLoader(new UpdateWaypointIconLoader());

        // Register inventory descriptors.
        InventoryConfigDAO dao = CraftVentoryLibrary.createDefaultConfigDAO(factory);

        this.inventoryService.createProvider(new WaypointsMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointIconsMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointDeleteMenuDescriptor(this, dao));

        // Load inventories.
        this.inventoryService.loadInventoryConfigs();
    }
}
