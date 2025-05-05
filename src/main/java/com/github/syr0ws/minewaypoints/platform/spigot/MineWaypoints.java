package com.github.syr0ws.minewaypoints.platform.spigot;

import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.config.action.ClickActionLoaderFactory;
import com.github.syr0ws.craftventory.api.config.dao.InventoryConfigDAO;
import com.github.syr0ws.craftventory.common.CraftVentoryLibrary;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.*;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.action.*;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointService;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointUserService;
import com.github.syr0ws.minewaypoints.plugin.business.service.BusinessWaypointActivationService;
import com.github.syr0ws.minewaypoints.plugin.business.service.BusinessWaypointService;
import com.github.syr0ws.minewaypoints.plugin.business.service.BusinessWaypointUserService;
import com.github.syr0ws.minewaypoints.plugin.business.service.impl.SimpleBusinessWaypointActivationService;
import com.github.syr0ws.minewaypoints.plugin.business.service.impl.SimpleBusinessWaypointService;
import com.github.syr0ws.minewaypoints.plugin.business.service.impl.SimpleBusinessWaypointUserService;
import com.github.syr0ws.minewaypoints.plugin.cache.WaypointOwnerCache;
import com.github.syr0ws.minewaypoints.plugin.cache.WaypointSharingRequestCache;
import com.github.syr0ws.minewaypoints.platform.spigot.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.infrastructure.cache.SimpleWaypointOwnerCache;
import com.github.syr0ws.minewaypoints.infrastructure.cache.SimpleWaypointSharingRequestCache;
import com.github.syr0ws.minewaypoints.platform.spigot.cache.impl.SimpleWaypointVisibleCache;
import com.github.syr0ws.minewaypoints.platform.spigot.command.CommandWaypoints;
import com.github.syr0ws.minewaypoints.plugin.persistence.WaypointDAO;
import com.github.syr0ws.minewaypoints.plugin.persistence.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.jdbc.JdbcWaypointDAO;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.jdbc.JdbcWaypointUserDAO;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnection;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionConfig;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionFactory;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnectionLoader;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.DatabaseInitializer;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.initializer.DatabaseInitializerFactory;
import com.github.syr0ws.minewaypoints.platform.spigot.integration.IntegrationService;
import com.github.syr0ws.minewaypoints.platform.spigot.integration.worldguard.WorldGuardIntegration;
import com.github.syr0ws.minewaypoints.platform.spigot.listener.WaypointActivationListener;
import com.github.syr0ws.minewaypoints.platform.spigot.listener.WaypointUserListener;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointOwner;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.platform.spigot.service.impl.SimpleBukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.spigot.service.impl.SimpleBukkitWaypointService;
import com.github.syr0ws.minewaypoints.platform.spigot.service.impl.SimpleBukkitWaypointUserService;
import com.github.syr0ws.minewaypoints.plugin.settings.WaypointSettings;
import com.github.syr0ws.minewaypoints.platform.spigot.settings.WaypointSettingsLoader;
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

    private WaypointOwnerCache<? extends WaypointOwner> waypointOwnerCache;
    private WaypointVisibleCache waypointVisibleCache;

    private InventoryService inventoryService;
    private IntegrationService integrationService;

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
        this.enableIntegrations();
    }

    @Override
    public void onDisable() {
        this.disableIntegrations();
        this.closeDatabaseConnection();

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

    private void closeDatabaseConnection() {

        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException exception) {
            this.getLogger().log(Level.SEVERE, "An error occurred while closing the database connection", exception);
        }
    }

    private void loadServices() throws ConfigurationException {

        WaypointSettings settings = new WaypointSettingsLoader().loadWaypointSettings(super.getConfig());

        // Cache
        WaypointOwnerCache<WaypointOwnerEntity> waypointOwnerCache = new SimpleWaypointOwnerCache();
        this.waypointOwnerCache = waypointOwnerCache;

        WaypointSharingRequestCache sharingRequestCache = new SimpleWaypointSharingRequestCache(this);
        this.waypointVisibleCache = new SimpleWaypointVisibleCache();

        // Waypoint DAO
        WaypointDAO waypointDAO = new JdbcWaypointDAO(this.connection);
        WaypointUserDAO waypointUserDAO = new JdbcWaypointUserDAO(this.connection, waypointDAO);

        // Business services
        BusinessWaypointUserService waypointUserService = new SimpleBusinessWaypointUserService(waypointUserDAO, waypointOwnerCache);
        BusinessWaypointService waypointService = new SimpleBusinessWaypointService(waypointDAO, waypointUserDAO, waypointOwnerCache, sharingRequestCache, settings);
        BusinessWaypointActivationService waypointActivationService = new SimpleBusinessWaypointActivationService(waypointDAO);

        // Platform services
        this.bukkitWaypointUserService = new SimpleBukkitWaypointUserService(this, waypointUserService);
        this.bukkitWaypointService = new SimpleBukkitWaypointService(this, waypointService, this.waypointOwnerCache, settings);
        this.bukkitWaypointActivationService = new SimpleBukkitWaypointActivationService(this, waypointActivationService, this.waypointVisibleCache);
    }

    private void registerCommands() {
        SmartCommandService service = SmartCommandLibrary.createService(this);
        service.registerCommand(new CommandWaypoints(this, this.inventoryService, this.bukkitWaypointService, this.waypointOwnerCache));
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

        this.inventoryService.createProvider(new WaypointsMenuDescriptor(this, dao, this.bukkitWaypointService, this.bukkitWaypointActivationService, this.waypointOwnerCache));
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

    private void enableIntegrations() {
        this.integrationService = new IntegrationService();
        this.integrationService.registerIntegration(new WorldGuardIntegration(this));
        this.integrationService.enableIntegrations();
    }

    private void disableIntegrations() {
        if(this.integrationService != null) {
            this.integrationService.disableIntegrations();
        }
    }
}
