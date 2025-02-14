package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.cache.impl.SimpleWaypointVisibleCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.service.WaypointVisibleService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SimpleWaypointVisibleService implements WaypointVisibleService {

    private final Plugin plugin;
    private final WaypointService waypointService;
    private final WaypointVisibleCache cache;

    public SimpleWaypointVisibleService(Plugin plugin, WaypointService waypointService) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(waypointService == null) {
            throw new IllegalArgumentException("waypointService cannot be null");
        }

        this.plugin = plugin;
        this.waypointService = waypointService;
        this.cache = new SimpleWaypointVisibleCache();

        PluginManager manager = this.plugin.getServer().getPluginManager();
        manager.registerEvents(new WaypointVisibleListener(), this.plugin);

        WaypointVisibleTask task = new WaypointVisibleTask();
        task.runTaskTimer(this.plugin, 0L, 20L);
    }

    @Override
    public void showWaypoint(Player player, Waypoint waypoint) {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        this.cache.showWaypoint(player, waypoint);
    }

    @Override
    public void hideWaypoint(Player player) {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        this.cache.hideWaypoint(player);
    }

    @Override
    public void hideAll() {
        this.cache.hideAll();
    }

    private class WaypointVisibleListener implements Listener {

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {

            Player player = event.getPlayer();
            // If the player has a visible waypoint, removing it from the cache.
            SimpleWaypointVisibleService.this.hideWaypoint(player);
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {

            Plugin plugin = event.getPlugin();
            // Hiding all the visible waypoints when the plugin shuts down.
            if(plugin.equals(SimpleWaypointVisibleService.this.plugin)) {
                SimpleWaypointVisibleService.this.hideAll();
            }
        }

        // TODO Show waypoints for online players when enabling the plugin

        @EventHandler
        public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();
            String world = player.getWorld().getName();

            // Hiding the current visible waypoint if any.
            SimpleWaypointVisibleService.this.hideWaypoint(player);

            // TODO Catch errors
            SimpleWaypointVisibleService.this.waypointService.getActivatedWaypoint(playerId, world)
                    .then(optional -> optional.ifPresent(waypoint -> SimpleWaypointVisibleService.this.showWaypoint(player, waypoint)))
                    .resolveAsync(SimpleWaypointVisibleService.this.plugin);
        }
    }

    private class WaypointVisibleTask extends BukkitRunnable {

        @Override
        public void run() {
            SimpleWaypointVisibleService.this.cache.getPlayerWithVisibleWaypoints().forEach(((player, waypoint) -> {
                // TODO
            }));
        }
    }
}
