package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.util.Promise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WaypointShareCache {

    private static final int CACHE_EXPIRATION_SECONDS = 120;

    private final ConcurrentHashMap<UUID, CacheData> cache = new ConcurrentHashMap<>();

    public WaypointShareCache(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, new CacheCleaner(), 0L, 20L * 10);
    }

    public UUID addSharingProposal(Promise<WaypointShare> promise) {

        if(promise == null) {
            throw new IllegalArgumentException("promise cannot be null");
        }

        UUID uuid = UUID.randomUUID();
        this.cache.put(uuid, new CacheData(System.currentTimeMillis(), promise));

        return uuid;
    }

    public void removeSharingProposal(UUID uuid) {

        if(uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        this.cache.remove(uuid);
    }

    public boolean hasSharingProposal(UUID uuid) {

        if(uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        return this.cache.containsKey(uuid);
    }

    public Promise<WaypointShare> getSharingProposal(UUID uuid) {

        if(uuid == null) {
            throw new IllegalArgumentException("uuid cannot be null");
        }

        CacheData cacheData = this.cache.get(uuid);

        if(cacheData == null) {
            throw new IllegalArgumentException("No sharing proposal in cache");
        }

        return this.cache.get(uuid).promise();
    }

    private record CacheData(long time, Promise<WaypointShare> promise) {

    }

    private class CacheCleaner implements Runnable {

        @Override
        public void run() {
            WaypointShareCache.this.cache.values().removeIf(data ->
                    (data.time() / 1000) + CACHE_EXPIRATION_SECONDS <= (System.currentTimeMillis() / 1000));
        }
    }
}
