package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.cache.impl.SimpleWaypointVisibleCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.service.WaypointActivationService;
import com.github.syr0ws.minewaypoints.service.util.WaypointEnums;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class SimpleWaypointActivationService implements WaypointActivationService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;
    private final WaypointVisibleCache cache;

    public SimpleWaypointActivationService(Plugin plugin, WaypointDAO waypointDAO) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointDAO, "waypointDAO cannot be null");

        this.plugin = plugin;
        this.waypointDAO = waypointDAO;
        this.cache = new SimpleWaypointVisibleCache();

        PluginManager manager = this.plugin.getServer().getPluginManager();
        manager.registerEvents(new WaypointVisibleListener(), this.plugin);

        WaypointVisibleTask task = new WaypointVisibleTask();
        task.runTaskTimer(this.plugin, 0L, 20L);
    }

    @Override
    public Promise<WaypointEnums.WaypointActivationStatus> activateWaypoint(Player player, long waypointId) {
        Validate.notNull(player, "player cannot be null");

        UUID playerId = player.getUniqueId();

        return new Promise<>((resolve, reject) -> {

            // Retrieving the waypoint.
            Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

            if(optional.isEmpty()) {
                resolve.accept(WaypointEnums.WaypointActivationStatus.WAYPOINT_NOT_FOUND);
                return;
            }

            WaypointEntity waypoint = optional.get();

            // A player must have access to the waypoint to activate it.
            if(!this.waypointDAO.hasAccessToWaypoint(playerId, waypointId)) {
                resolve.accept(WaypointEnums.WaypointActivationStatus.NO_WAYPOINT_ACCESS);
                return;
            }

            // At most one waypoint can be activated for a player in a world.
            // Here, we deactivate any other activated waypoint for the player in the given world.
            this.waypointDAO.deactivateWaypoint(playerId, waypoint.getLocation().getWorld());

            // Activating the waypoint.
            this.waypointDAO.activateWaypoint(playerId, waypointId);
            resolve.accept(WaypointEnums.WaypointActivationStatus.ACTIVATED);

            String playerWorld = player.getWorld().getName();
            String waypointWorld = waypoint.getLocation().getWorld();

            if(playerWorld.equals(waypointWorld)) {
                this.showWaypoint(player, waypoint);
            }
        });
    }

    @Override
    public Promise<Void> deactivateWaypoint(Player player, long waypointId) {
        Validate.notNull(player, "player cannot be null");

        UUID playerId = player.getUniqueId();

        return new Promise<>((resolve, reject) -> {
            this.waypointDAO.deactivateWaypoint(playerId, waypointId);
            resolve.accept(null);
        });
    }

    @Override
    public Promise<Optional<Waypoint>> getActivatedWaypoint(Player player, String world) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(world, "world cannot be null");

        UUID playerId = player.getUniqueId();

        return new Promise<>((resolve, reject) -> {
            Optional<Waypoint> optional = this.waypointDAO.findActivatedWaypoint(playerId, world)
                    .map(entity -> entity);
            resolve.accept(optional);
        });
    }

    @Override
    public void showWaypoint(Player player, Waypoint waypoint) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(waypoint, "waypoint cannot be null");

        this.cache.showWaypoint(player, waypoint);
    }

    @Override
    public void hideWaypoint(Player player) {
        Validate.notNull(player, "player cannot be null");

        this.cache.hideWaypoint(player);
    }

    @Override
    public void hideAll() {
        this.cache.hideAll();
    }

    private class WaypointVisibleListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {

            Player player = event.getPlayer();
            World world = player.getWorld();

            this.showWaypointIfAny(player, world);
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {

            Player player = event.getPlayer();
            // If the player has a visible waypoint, removing it from the cache.
            SimpleWaypointActivationService.this.hideWaypoint(player);
        }

        @EventHandler
        public void onPluginEnable(PluginEnableEvent event) {

            Plugin plugin = event.getPlugin();

            // Showing waypoints for all the online players with an activated waypoint in their current world.
            if(plugin.equals(SimpleWaypointActivationService.this.plugin)) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    this.showWaypointIfAny(player, player.getWorld());
                });
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {

            Plugin plugin = event.getPlugin();

            // Hiding all the visible waypoints when the plugin shuts down.
            if(plugin.equals(SimpleWaypointActivationService.this.plugin)) {
                SimpleWaypointActivationService.this.hideAll();
            }
        }

        @EventHandler
        public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

            Player player = event.getPlayer();
            World world = player.getWorld();

            // Hiding the current visible waypoint if any.
            SimpleWaypointActivationService.this.hideWaypoint(player);

            this.showWaypointIfAny(player, world);
        }

        private void showWaypointIfAny(Player player, World world) {
            SimpleWaypointActivationService.this.getActivatedWaypoint(player, world.getName())
                    .then(optional ->
                            optional.ifPresent(waypoint -> SimpleWaypointActivationService.this.showWaypoint(player, waypoint)))
                    .except(throwable -> {
                        String message = String.format("An error occurred while retrieving activated waypoint for player %s", player.getUniqueId());
                        SimpleWaypointActivationService.this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                    })
                    .resolveAsync(SimpleWaypointActivationService.this.plugin);
        }
    }

    private class WaypointVisibleTask extends BukkitRunnable {

        @Override
        public void run() {
            SimpleWaypointActivationService.this.cache.getPlayerWithVisibleWaypoints().forEach(((player, waypoint) -> {
                // TODO
                System.out.println(String.format("Showing waypoint %s to %s", waypoint.getName(), player.getName()));
            }));
        }
    }
}
