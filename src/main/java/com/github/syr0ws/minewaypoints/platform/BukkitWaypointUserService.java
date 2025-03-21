package com.github.syr0ws.minewaypoints.platform;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface BukkitWaypointUserService {

    Promise<WaypointOwner> createDataIfNotExists(Player player);

    Promise<Boolean> hasData(Player player);

    Promise<Optional<WaypointOwner>> getWaypointOwner(Player player);
}
