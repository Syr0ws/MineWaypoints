package com.github.syr0ws.minewaypoints.listener;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.logging.Level;

public class PlayerListener implements Listener {

    private final Plugin plugin;
    private final WaypointUserService waypointUserService;

    public PlayerListener(Plugin plugin, WaypointUserService waypointUserService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointUserService, "waypointUserService cannot be null");

        this.plugin = plugin;
        this.waypointUserService = waypointUserService;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        this.waypointUserService.hasData(player.getUniqueId())
                .then(hasData -> {
                    if (hasData) {
                        this.waypointUserService.loadData(playerId)
                                .except(throwable -> {
                                    String message = String.format("An error occurred while loading data for player %s", playerId);
                                    this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                                })
                                .resolveAsync(this.plugin);
                    } else {
                        this.waypointUserService.createData(playerId, player.getName())
                                .except(throwable -> {
                                    String message = String.format("An error occurred while creating data for player %s", playerId);
                                    this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                                })
                                .resolveAsync(this.plugin);
                    }
                })
                .except(throwable -> {
                    String message = String.format("An error occurred while checking if the player %s has data", playerId);
                    this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                })
                .resolveAsync(this.plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.waypointUserService.unloadData(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPluginEnable(PluginEnableEvent event) {

        Plugin plugin = event.getPlugin();

        if (!plugin.equals(this.plugin)) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player ->
                this.waypointUserService.loadData(player.getUniqueId()).resolveAsync(this.plugin));
    }
}
