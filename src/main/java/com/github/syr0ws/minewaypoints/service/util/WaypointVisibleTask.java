package com.github.syr0ws.minewaypoints.service.util;

import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WaypointVisibleTask extends BukkitRunnable {

    private final Plugin plugin;
    private final WaypointVisibleCache cache;

    public WaypointVisibleTask(Plugin plugin, WaypointVisibleCache cache) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(cache == null) {
            throw new IllegalArgumentException("cache cannot be null");
        }

        this.plugin = plugin;
        this.cache = cache;
    }

    public void start() {
        this.runTaskTimer(this.plugin, 0L, 20L);
    }

    public void stop() {
        this.cancel();
    }

    @Override
    public void run() {

        this.cache.getPlayerWithVisibleWaypoints().forEach((player, waypoint) -> {
            // TODO Send waypoint in action bar.
        });
    }
}
