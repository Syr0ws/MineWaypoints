package com.github.syr0ws.minewaypoints.listener;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.api.event.AsyncWaypointDeleteEvent;
import com.github.syr0ws.minewaypoints.api.event.AsyncWaypointUnshareEvent;
import com.github.syr0ws.minewaypoints.api.event.AsyncWaypointUpdateEvent;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointActivationService;
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

import java.util.logging.Level;

public class WaypointActivationListener implements Listener {

    private final Plugin plugin;
    private final BukkitWaypointActivationService waypointActivationService;
    private final WaypointVisibleCache waypointVisibleCache;

    public WaypointActivationListener(Plugin plugin, BukkitWaypointActivationService waypointActivationService, WaypointVisibleCache waypointVisibleCache) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");
        Validate.notNull(waypointVisibleCache, "waypointVisibleCache cannot be null");

        this.plugin = plugin;
        this.waypointActivationService = waypointActivationService;
        this.waypointVisibleCache = waypointVisibleCache;
    }

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
        this.waypointVisibleCache.hideWaypoint(player);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

        Plugin plugin = event.getPlugin();

        // Showing waypoints for all the online players with an activated waypoint in their current world.
        if (plugin.equals(this.plugin)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                this.showWaypointIfAny(player, player.getWorld());
            });
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {

        Plugin plugin = event.getPlugin();

        // Hiding all the visible waypoints when the plugin shuts down.
        if (plugin.equals(this.plugin)) {
            this.waypointVisibleCache.hideAll();
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();
        World world = player.getWorld();

        // Hiding the current visible waypoint if any.
        this.waypointVisibleCache.hideWaypoint(player);

        this.showWaypointIfAny(player, world);
    }

    @EventHandler
    public void onWaypointDelete(AsyncWaypointDeleteEvent event) {
        Waypoint waypoint = event.getWaypoint();

        // The deleted waypoint should no longer be visible to players it has been shared with.
        // Note: As the waypoint is deleted, there is no need to deactivate it as data will be automatically cleared.
        this.waypointVisibleCache.hideWaypoint(waypoint);
    }

    @EventHandler
    public void onWaypointUnshare(AsyncWaypointUnshareEvent event) {

        Waypoint waypoint = event.getWaypoint();
        WaypointUser sharedWith = event.getSharedWith();
        Player playerSharedWith = sharedWith.getPlayer();

        // Hiding the waypoint if it is visible to the player it has been shared with.
        if (playerSharedWith != null) {
            this.waypointVisibleCache.hideWaypoint(playerSharedWith, waypoint);
        }
    }

    @EventHandler
    public void onWaypointUpdate(AsyncWaypointUpdateEvent event) {
        Waypoint waypoint = event.getWaypoint();

        // Propagating the update to the other instances of the same waypoint.
        this.waypointVisibleCache.getPlayerWithVisibleWaypoints().values().stream()
                .filter(visible -> visible.getId() == waypoint.getId())
                .forEach(visible -> {
                    visible.setName(waypoint.getName());
                    visible.setIcon(waypoint.getIcon());
                    visible.setLocation(waypoint.getLocation());
                });
    }

    private void showWaypointIfAny(Player player, World world) {
        this.waypointActivationService.getActivatedWaypoint(player.getUniqueId(), world)
                .then(optional ->
                        optional.ifPresent(waypoint -> {
                            this.waypointVisibleCache.showWaypoint(player, waypoint);
                        }))
                .except(throwable -> {
                    String message = String.format("An error occurred while retrieving activated waypoint for player %s", player.getUniqueId());
                    this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                })
                .resolveAsync(this.plugin);
    }
}
