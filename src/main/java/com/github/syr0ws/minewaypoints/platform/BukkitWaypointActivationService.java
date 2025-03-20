package com.github.syr0ws.minewaypoints.platform;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BukkitWaypointActivationService {

    Promise<BusinessResult<Waypoint, BusinessFailure>> activateWaypoint(Player player, long waypointId) throws WaypointDataException;

    Promise<BusinessResult<Waypoint, BusinessFailure>> deactivateWaypoint(Player player, long waypointId) throws WaypointDataException;

    Promise<Optional<Waypoint>> getActivatedWaypoint(UUID playerId, World world) throws WaypointDataException;

    Promise<Set<Long>> getActivatedWaypointIds(UUID playerId) throws WaypointDataException;
}
