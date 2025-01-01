package com.github.syr0ws.minewaypoints.listener;

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

import java.util.logging.Level;

public class PlayerListener implements Listener {

    private final Plugin plugin;
    private final WaypointUserService waypointUserService;

    public PlayerListener(Plugin plugin, WaypointUserService waypointUserService) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(waypointUserService == null) {
            throw new IllegalArgumentException("waypointUserService cannot be null");
        }

        this.plugin = plugin;
        this.waypointUserService = waypointUserService;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        this.waypointUserService.hasData(player.getUniqueId())
                .onSuccess(hasData -> {
                    if(hasData) {
                        this.waypointUserService.loadData(player.getUniqueId()).resolve();
                    } else {
                        this.waypointUserService.createData(player.getUniqueId(), player.getName()).resolve();
                    }
                })
                .onError(error ->
                        this.plugin.getLogger().log(Level.SEVERE, "An error occurred while loading player data", error))
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

        if(!plugin.equals(this.plugin)) {
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player ->
                this.waypointUserService.loadData(player.getUniqueId()).resolveAsync(this.plugin));
    }
}
