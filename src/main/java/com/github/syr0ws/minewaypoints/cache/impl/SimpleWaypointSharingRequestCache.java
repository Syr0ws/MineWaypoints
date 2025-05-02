package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.cache.WaypointSharingRequestCache;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointSharingRequest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWaypointSharingRequestCache implements WaypointSharingRequestCache {

    private static final int CACHE_EXPIRATION_SECONDS = 120;

    private final ConcurrentHashMap<UUID, WaypointSharingRequest> requests = new ConcurrentHashMap<>();

    public SimpleWaypointSharingRequestCache(Plugin plugin) {
        Validate.notNull(plugin, "plugin cannot be null");
        Bukkit.getScheduler().runTaskTimer(plugin, new CacheCleaner(), 0L, 20L * 10L); // 10 seconds
    }

    @Override
    public void addSharingRequest(WaypointSharingRequest request) {
        Validate.notNull(request, "request cannot be null");
        this.requests.put(request.requestId(), request);
    }

    @Override
    public void removeSharingRequest(UUID requestId) {
        Validate.notNull(requestId, "requestId cannot be null");
        this.requests.remove(requestId);
    }

    @Override
    public boolean hasSharingRequest(UUID requestId) {
        Validate.notNull(requestId, "requestId cannot be null");
        return this.requests.containsKey(requestId);
    }

    @Override
    public Optional<WaypointSharingRequest> getSharingRequest(UUID requestId) {
        Validate.notNull(requestId, "requestId cannot be null");
        return Optional.ofNullable(this.requests.get(requestId));
    }

    private class CacheCleaner implements Runnable {

        @Override
        public void run() {
            SimpleWaypointSharingRequestCache.this.requests.values().removeIf(request ->
                    (request.createdAt() / 1000) + CACHE_EXPIRATION_SECONDS <= (System.currentTimeMillis() / 1000));
        }
    }
}
