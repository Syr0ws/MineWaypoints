package com.github.syr0ws.minewaypoints.platform;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointOwner;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface BukkitWaypointUserService {

    Promise<WaypointOwner> loadData(Player player);

    Promise<Void> unloadData(Player player);

    Promise<WaypointOwner> createDataIfNotExists(Player player);

    Promise<Optional<WaypointOwner>> getWaypointOwner(Player player);
}
