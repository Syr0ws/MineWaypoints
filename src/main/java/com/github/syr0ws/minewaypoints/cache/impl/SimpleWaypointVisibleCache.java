package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleWaypointVisibleCache implements WaypointVisibleCache {

    private final Map<Player, Waypoint> visibleWaypoints = new HashMap<>();

    @Override
    public void showWaypoint(Player player, Waypoint waypoint) {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        if(waypoint == null) {
            throw new IllegalArgumentException("waypoint cannot be null");
        }

        this.visibleWaypoints.put(player, waypoint);
    }

    @Override
    public void hideWaypoint(Player player) {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        this.visibleWaypoints.remove(player);
    }

    @Override
    public boolean hasVisibleWaypoint(Player player) {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        return this.visibleWaypoints.containsKey(player);
    }

    @Override
    public Optional<Waypoint> getVisibleWaypoint(Player player) {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        return Optional.ofNullable(this.visibleWaypoints.get(player));
    }

    @Override
    public Map<Player, Waypoint> getPlayerWithVisibleWaypoints() {
        return Collections.unmodifiableMap(this.visibleWaypoints);
    }
}
