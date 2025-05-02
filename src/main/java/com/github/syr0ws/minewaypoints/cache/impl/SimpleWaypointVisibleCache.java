package com.github.syr0ws.minewaypoints.cache.impl;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleWaypointVisibleCache implements WaypointVisibleCache {

    private final Map<Player, Waypoint> visibleWaypoints = new HashMap<>();

    @Override
    public void showWaypoint(Player player, Waypoint waypoint) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(waypoint, "waypoint cannot be null");

        this.visibleWaypoints.put(player, waypoint);
    }

    @Override
    public void hideWaypoint(Player player) {
        Validate.notNull(player, "player cannot be null");
        this.visibleWaypoints.remove(player);
    }

    @Override
    public void hideWaypoint(Waypoint waypoint) {
        Validate.notNull(waypoint, "waypoint cannot be null");
        this.visibleWaypoints.values().removeIf(wp -> wp.getId() == waypoint.getId());
    }

    @Override
    public void hideWaypoint(Player player, Waypoint waypoint) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(waypoint, "waypoint cannot be null");
        this.visibleWaypoints.entrySet().removeIf(entry -> entry.getKey().equals(player)
                && entry.getValue().getId() == waypoint.getId());
    }

    @Override
    public void hideAll() {
        this.visibleWaypoints.clear();
    }

    @Override
    public boolean hasVisibleWaypoint(Player player) {
        Validate.notNull(player, "player cannot be null");

        return this.visibleWaypoints.containsKey(player);
    }

    @Override
    public Optional<Waypoint> getVisibleWaypoint(Player player) {
        Validate.notNull(player, "player cannot be null");

        return Optional.ofNullable(this.visibleWaypoints.get(player));
    }

    @Override
    public Map<Player, Waypoint> getPlayerWithVisibleWaypoints() {
        return Collections.unmodifiableMap(this.visibleWaypoints);
    }
}
