package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public interface WaypointVisibleCache {

    void showWaypoint(Player player, Waypoint waypoint);

    void hideWaypoint(Player player);

    void hideWaypoint(Waypoint waypoint);

    void hideAll();

    boolean hasVisibleWaypoint(Player player);

    Optional<Waypoint> getVisibleWaypoint(Player player);

    Map<Player, Waypoint> getPlayerWithVisibleWaypoints();
}
