package com.github.syr0ws.minewaypoints.listener;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.util.Callback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

        this.waypointUserService.hasDataAsync(player.getUniqueId(), new Callback<>() {

            @Override
            public void onSuccess(Boolean value) {
                PlayerListener.this.loadData(player, value);
            }

            @Override
            public void onError(Throwable throwable) {
                PlayerListener.this.plugin.getLogger().log(Level.SEVERE, "An error occurred while loading player data", throwable);
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.waypointUserService.unloadData(player.getUniqueId());
    }

    private void loadData(Player player, boolean hasData) {

        try {
            if(hasData) {
                this.waypointUserService.loadData(player.getUniqueId());
            } else {
                this.waypointUserService.createData(player.getUniqueId(), player.getName());
            }
        } catch (WaypointDataException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while loading player data", exception);
        }
    }
}
