package com.github.syr0ws.minewaypoints;

import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.config.action.ClickActionLoaderFactory;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.common.CraftVentoryLibrary;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointActivationService;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointService;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointUserService;
import com.github.syr0ws.minewaypoints.business.service.impl.SimpleBusinessWaypointActivationService;
import com.github.syr0ws.minewaypoints.business.service.impl.SimpleBusinessWaypointService;
import com.github.syr0ws.minewaypoints.business.service.impl.SimpleBusinessWaypointUserService;
import com.github.syr0ws.minewaypoints.business.settings.WaypointSettings;
import com.github.syr0ws.minewaypoints.cache.WaypointSharingRequestCache;
import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.cache.impl.SimpleWaypointSharingRequestCache;
import com.github.syr0ws.minewaypoints.cache.impl.SimpleWaypointVisibleCache;
import com.github.syr0ws.minewaypoints.command.CommandWaypoints;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.dao.jdbc.JdbcWaypointDAO;
import com.github.syr0ws.minewaypoints.dao.jdbc.JdbcWaypointUserDAO;
import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnection;
import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionFactory;
import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnectionLoader;
import com.github.syr0ws.minewaypoints.database.initializer.DatabaseInitializer;
import com.github.syr0ws.minewaypoints.database.initializer.DatabaseInitializerFactory;
import com.github.syr0ws.minewaypoints.listener.WaypointActivationListener;
import com.github.syr0ws.minewaypoints.listener.WaypointUserListener;
import com.github.syr0ws.minewaypoints.menu.*;
import com.github.syr0ws.minewaypoints.menu.action.*;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointUserService;
import com.github.syr0ws.minewaypoints.platform.impl.SimpleBukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.impl.SimpleBukkitWaypointService;
import com.github.syr0ws.minewaypoints.platform.impl.SimpleBukkitWaypointUserService;
import com.github.syr0ws.minewaypoints.util.WaypointSettingsLoader;
import com.github.syr0ws.smartcommands.api.SmartCommandLibrary;
import com.github.syr0ws.smartcommands.api.SmartCommandService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class MineWaypoints extends JavaPlugin {

    private BukkitWaypointUserService bukkitWaypointUserService;
    private BukkitWaypointService bukkitWaypointService;
    private BukkitWaypointActivationService bukkitWaypointActivationService;

    private WaypointVisibleCache waypointVisibleCache;

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

        try {
            this.loadServices();
        } catch (ConfigurationException exception) {
            this.getLogger().log(Level.SEVERE, "An error occurred with the configuration", exception);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.registerInventoryProviders();
        this.registerCommands();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
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

        DatabaseInitializer initializer = DatabaseInitializerFactory.getInitializer(this, config);
        initializer.init();

        DatabaseConnectionFactory factory = new DatabaseConnectionFactory(this);

        this.connection = factory.createDatabaseConnection(config);
        this.connection.open();
    }

    private void loadServices() throws ConfigurationException {

        WaypointSettings settings = new WaypointSettingsLoader().loadWaypointSettings(super.getConfig());

        // Cache
        WaypointSharingRequestCache sharingRequestCache = new SimpleWaypointSharingRequestCache(this);
        this.waypointVisibleCache = new SimpleWaypointVisibleCache();

        // Waypoint DAO
        WaypointDAO waypointDAO = new JdbcWaypointDAO(this.connection);
        WaypointUserDAO waypointUserDAO = new JdbcWaypointUserDAO(this.connection, waypointDAO);

        // Business services
        BusinessWaypointUserService waypointUserService = new SimpleBusinessWaypointUserService(waypointUserDAO);
        BusinessWaypointService waypointService = new SimpleBusinessWaypointService(waypointDAO, waypointUserDAO, sharingRequestCache, settings);
        BusinessWaypointActivationService waypointActivationService = new SimpleBusinessWaypointActivationService(waypointDAO);

        // Platform services
        this.bukkitWaypointUserService = new SimpleBukkitWaypointUserService(this, waypointUserService);
        this.bukkitWaypointService = new SimpleBukkitWaypointService(this, waypointService);
        this.bukkitWaypointActivationService = new SimpleBukkitWaypointActivationService(this, waypointActivationService, this.waypointVisibleCache);
    }

    private void registerCommands() {
        SmartCommandService service = SmartCommandLibrary.createService(this);
        service.registerCommand(new CommandWaypoints(this, this.inventoryService, this.bukkitWaypointService));
    }

    private void registerListeners() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new WaypointUserListener(this, this.bukkitWaypointUserService), this);
        manager.registerEvents(new WaypointActivationListener(this, this.bukkitWaypointActivationService, this.waypointVisibleCache), this);
    }

    private void registerInventoryProviders() {

        this.inventoryService = CraftVentoryLibrary.createInventoryService(this);

        // Register action loaders.
        ClickActionLoaderFactory<ConfigurationSection> factory =
                CraftVentoryLibrary.createDefaultClickActionLoaderFactory();

        factory.addLoader(new OpenEditWaypointMenuLoader());
        factory.addLoader(new OpenWaypointIconsMenuLoader());
        factory.addLoader(new OpenWaypointDeleteMenuLoader());
        factory.addLoader(new OpenWaypointSharedWithMenuLoader());
        factory.addLoader(new OpenSharedWaypointDeleteMenuLoader());
        factory.addLoader(new OpenWaypointUnshareMenuLoader());
        factory.addLoader(new UpdateWaypointIconLoader(this, this.bukkitWaypointService));
        factory.addLoader(new DeleteWaypointLoader(this, this.bukkitWaypointService));
        factory.addLoader(new UnshareWaypointLoader(this, this.bukkitWaypointService));
        factory.addLoader(new RemoveSharedWaypointLoader(this, this.bukkitWaypointService));
        factory.addLoader(new ToggleWaypointActivationLoader(this, this.bukkitWaypointActivationService));

        // Register inventory descriptors.
        InventoryConfigDAO dao = CraftVentoryLibrary.createDefaultConfigDAO(factory);

        this.inventoryService.createProvider(new WaypointsMenuDescriptor(this, dao, this.bukkitWaypointUserService, this.bukkitWaypointService, this.bukkitWaypointActivationService));
        this.inventoryService.createProvider(new WaypointEditMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointIconsMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointDeleteMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new WaypointSharedWithMenuDescriptor(this, dao, this.bukkitWaypointService));
        this.inventoryService.createProvider(new WaypointUnshareMenuDescriptor(this, dao));
        this.inventoryService.createProvider(new SharedWaypointsMenuDescriptor(this, dao, this.bukkitWaypointService, this.bukkitWaypointActivationService));
        this.inventoryService.createProvider(new SharedWaypointDeleteMenuDescriptor(this, dao));

        // Load inventories.
        this.inventoryService.loadInventoryConfigs();
    }
}
