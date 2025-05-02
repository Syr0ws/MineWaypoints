package com.github.syr0ws.minewaypoints.platform.spigot;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BukkitWaypointActivationService {

    Promise<BusinessResult<Waypoint, BusinessFailure>> activateWaypoint(Player player, long waypointId);

    Promise<BusinessResult<Waypoint, BusinessFailure>> deactivateWaypoint(Player player, long waypointId);

    Promise<Optional<Waypoint>> getActivatedWaypoint(UUID playerId, World world);

    Promise<Set<Long>> getActivatedWaypointIds(UUID playerId);
}
