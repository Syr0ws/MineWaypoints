package com.github.syr0ws.minewaypoints.platform;

import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointSharingRequest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface BukkitWaypointService {

    Promise<BusinessResult<Waypoint, ?>> createWaypoint(Player owner, String name, Material icon, Location location);

    Promise<BusinessResult<Waypoint, ?>> updateWaypointNameByName(Player owner, String waypointName, String newName);

    Promise<BusinessResult<Waypoint, ?>> updateWaypointLocationByName(Player owner, String waypointName, Location location);

    Promise<BusinessResult<Waypoint, ?>> updateWaypointIconById(Player owner, long waypointId, Material icon);

    Promise<BusinessResult<Void, ?>> deleteWaypoint(Player owner, long waypointId);

    Promise<BusinessResult<WaypointSharingRequest, ?>> sendWaypointSharingRequest(Player owner, String waypointName, Player target);

    Promise<BusinessResult<WaypointShare, ?>> acceptWaypointSharingRequest(Player player, UUID requestId);

    Promise<BusinessResult<WaypointSharingRequest, ?>> cancelWaypointSharingRequest(Player player, UUID requestId);

    Promise<BusinessResult<Void, ?>> unshareWaypointByOwner(Player owner, long waypointId, UUID targetId);

    Promise<List<WaypointShare>> getSharedWaypoints(Player player);

    Promise<List<WaypointShare>> getSharedWith(Player owner, long waypointId);
}
