package com.github.syr0ws.minewaypoints.listener;

import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.event.WaypointDeleteEvent;
import com.github.syr0ws.minewaypoints.event.WaypointUnshareEvent;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointActivationService;
import com.github.syr0ws.minewaypoints.service.impl.SimpleWaypointActivationService;
import com.github.syr0ws.minewaypoints.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class WaypointActivationListener implements Listener {

    private final Plugin plugin;
    private final WaypointActivationService waypointActivationService;

    public WaypointActivationListener(Plugin plugin, WaypointActivationService waypointActivationService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.plugin = plugin;
        this.waypointActivationService = waypointActivationService;
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
        this.waypointActivationService.hideWaypoint(player);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {

        Plugin plugin = event.getPlugin();

        // Showing waypoints for all the online players with an activated waypoint in their current world.
        if(plugin.equals(this.plugin)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                this.showWaypointIfAny(player, player.getWorld());
            });
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {

        Plugin plugin = event.getPlugin();

        // Hiding all the visible waypoints when the plugin shuts down.
        if(plugin.equals(this.plugin)) {
            this.waypointActivationService.hideAll();
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();
        World world = player.getWorld();

        // Hiding the current visible waypoint if any.
        this.waypointActivationService.hideWaypoint(player);

        this.showWaypointIfAny(player, world);
    }

    @EventHandler
    public void onWaypointDelete(WaypointDeleteEvent event) {

        Waypoint waypoint = event.getWaypoint();
        Set<WaypointUser> waypointSharedWith = event.getWaypointSharedWith();

        // Sending a message to online players with the deleted waypoint shared to indicate that it has been deleted.
        FileConfiguration config = this.plugin.getConfig();
        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);

        waypointSharedWith.stream()
                .map(WaypointUser::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> MessageUtil.sendMessage(player, config, "messages.waypoint.shared-deleted", placeholders));
    }

    @EventHandler
    public void onWaypointUnshare(WaypointUnshareEvent event) {

        Waypoint waypoint = event.getWaypoint();
        WaypointUser sharedWith = event.getSharedWith();

        // Sending a message to the user the waypoint is shared with if it is online to indicate
        // that the waypoint has been unshared.
        Player playerSharedWith = sharedWith.getPlayer();

        if(playerSharedWith != null) {

            FileConfiguration config = this.plugin.getConfig();
            Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);

            MessageUtil.sendMessage(playerSharedWith, config, "messages.waypoint.unshare-target", placeholders);
        }
    }

    private void showWaypointIfAny(Player player, World world) {
        this.waypointActivationService.getActivatedWaypoint(player, world.getName())
                .then(optional ->
                        optional.ifPresent(waypoint -> this.waypointActivationService.showWaypoint(player, waypoint)))
                .except(throwable -> {
                    String message = String.format("An error occurred while retrieving activated waypoint for player %s", player.getUniqueId());
                    this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                })
                .resolveAsync(this.plugin);
    }
}
