package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WaypointShareCache {

    private static final int CACHE_EXPIRATION_SECONDS = 120;

    private final ConcurrentHashMap<UUID, WaypointSharingRequest> cache = new ConcurrentHashMap<>();

    public WaypointShareCache(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, new CacheCleaner(), 0L, 20L * 10);
    }

    public UUID addSharingRequest(Waypoint waypoint, Player to) {

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        if(to == null) {
            throw new IllegalArgumentException("to cannot be null");
        }

        UUID uuid = UUID.randomUUID();
        WaypointSharingRequest request = new WaypointSharingRequest(waypoint, to, System.currentTimeMillis());

        this.cache.put(uuid, request);

        return uuid;
    }

    public void removeSharingRequest(UUID uuid) {

        if(uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        this.cache.remove(uuid);
    }

    public boolean hasSharingRequest(UUID uuid) {

        if(uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        return this.cache.containsKey(uuid);
    }

    public WaypointSharingRequest getSharingRequest(UUID uuid) {

        if(uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        return this.cache.get(uuid);
    }

    public record WaypointSharingRequest(Waypoint waypoint, Player to, long createdAt) {

    }

    private class CacheCleaner implements Runnable {

        @Override
        public void run() {
            WaypointShareCache.this.cache.values().removeIf(data ->
                    (data.createdAt() / 1000) + CACHE_EXPIRATION_SECONDS <= (System.currentTimeMillis() / 1000));
        }
    }
}
