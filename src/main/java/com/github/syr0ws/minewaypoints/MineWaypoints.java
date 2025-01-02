package com.github.syr0ws.minewaypoints;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.config.action.ClickActionLoaderFactory;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.common.CraftVentoryLibrary;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.cache.impl.SimpleWaypointUserCache;
import com.github.syr0ws.minewaypoints.command.CommandWaypoints;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.dao.jdbc.JdbcWaypointDAO;
import com.github.syr0ws.minewaypoints.dao.jdbc.JdbcWaypointUserDAO;
import com.github.syr0ws.minewaypoints.database.*;
import com.github.syr0ws.minewaypoints.listener.PlayerListener;
import com.github.syr0ws.minewaypoints.menu.WaypointDeleteMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.WaypointEditMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.WaypointIconsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.action.*;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.WaypointUserEntity;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.service.impl.SimpleWaypointService;
import com.github.syr0ws.minewaypoints.service.impl.SimpleWaypointUserService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class MineWaypoints extends JavaPlugin {

    private WaypointService waypointService;
    private WaypointUserService waypointUserService;

    private WaypointUserCache<? extends WaypointUser> waypointUserCache;
    private InventoryService inventoryService;

    private DatabaseConnection connection;

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

        this.loadServices();
        this.registerInventoryProviders();
        this.registerCommands();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        try {
            this.connection.closeConnection();
        } catch (SQLException exception) {
            this.getLogger().log(Level.SEVERE, "An error occurred while closing the database connection", exception);
        }
    }

    private void loadConfiguration() {
        super.saveDefaultConfig();
    }

    private void loadDatabase() throws Exception {

        DatabaseConnectionLoader loader = new DatabaseConnectionLoader(this);
        DatabaseConnectionConfig config = loader.loadConfig();

        DatabaseConnectionFactory factory = new DatabaseConnectionFactory(this);

        this.connection = factory.createDatabaseConnection(config.getDriver());
        this.connection.openConnection(config);

        DatabaseInitializer initializer = new DatabaseInitializer(this, this.connection);
        initializer.init();
    }

    private void loadServices() {

        WaypointDAO waypointDAO = new JdbcWaypointDAO(this.connection);
        WaypointUserDAO waypointUserDAO = new JdbcWaypointUserDAO(this.connection, waypointDAO);

        WaypointUserCache<WaypointUserEntity> waypointUserCache = new SimpleWaypointUserCache();
        this.waypointUserCache = waypointUserCache;

        this.waypointUserService = new SimpleWaypointUserService(waypointUserDAO, waypointUserCache);
        this.waypointService = new SimpleWaypointService(this, waypointDAO, waypointUserCache);
    }

    private void registerCommands() {
        super.getCommand("waypoints").setExecutor(
                new CommandWaypoints(this, inventoryService, this.waypointService, this.waypointUserCache)
        );
    }

    private void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerListener(this, this.waypointUserService), this);
    }

    private void registerInventoryProviders() {

        this.inventoryService = CraftVentoryLibrary.createInventoryService(this);

        // Register action loaders.
        ClickActionLoaderFactory<ConfigurationSection> factory =
                CraftVentoryLibrary.createDefaultClickActionLoaderFactory();

        factory.addLoader(new OpenEditWaypointMenuLoader());
        factory.addLoader(new OpenWaypointIconsMenuLoader());
        factory.addLoader(new OpenDeleteWaypointMenuLoader());
        factory.addLoader(new UpdateWaypointIconLoader(this, this.waypointService));
        factory.addLoader(new DeleteWaypointLoader(this, this.waypointService));

        // Register inventory descriptors.
        InventoryConfigDAO dao = CraftVentoryLibrary.createDefaultConfigDAO(factory);

        this.inventoryService.createProvider(new WaypointsMenuDescriptor(this, dao, this.waypointUserService));
        this.inventoryService.createProvider(new WaypointEditMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointIconsMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointDeleteMenuDescriptor(this, dao));

        // Load inventories.
        this.inventoryService.loadInventoryConfigs();
    }
}
