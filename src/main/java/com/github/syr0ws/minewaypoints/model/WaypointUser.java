package com.github.syr0ws.minewaypoints.model;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface WaypointUser {

    UUID getId();

    String getName();

    Player getPlayer();
}
