package com.github.syr0ws.minewaypoints.listener;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointUserService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class WaypointUserListener implements Listener {

    private final Plugin plugin;
    private final BukkitWaypointUserService waypointUserService;

    public WaypointUserListener(Plugin plugin, BukkitWaypointUserService waypointUserService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointUserService, "waypointUserService cannot be null");

        this.plugin = plugin;
        this.waypointUserService = waypointUserService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.waypointUserService.createDataIfNotExists(player).resolveAsync(this.plugin);
    }
}
