package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.entity.Player;

public interface WaypointActivationService {

    void showWaypoint(Player player, Waypoint waypoint);

    void hideWaypoint(Player player);

    void hideAll();
}
