package com.github.syr0ws.minewaypoints.service.platform;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface BukkitWaypointService {

    Optional<Promise<Waypoint>> createWaypoint(Player owner, String waypointName, Location location, Material icon);

    Optional<Promise<Void>> updateWaypointName(Player owner, String waypointName, String newWaypointName);

    Optional<Promise<Void>> updateWaypointLocation(Player owner, String waypointName, Location location);

    Optional<Promise<Void>> shareWaypoint(Player owner, String waypointName, String targetName);

    Optional<Promise<Boolean>> unshareWaypoint(Player owner, String waypointName, String targetName);
}
