package com.github.syr0ws.minewaypoints.platform;

import com.github.syr0ws.crafter.business.BusinessFailure;
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

    Promise<BusinessResult<Waypoint, BusinessFailure>> createWaypoint(Player owner, String name, Material icon, Location location);

    Promise<BusinessResult<Waypoint, BusinessFailure>> updateWaypointNameByName(Player owner, String waypointName, String newName);

    Promise<BusinessResult<Waypoint, BusinessFailure>> updateWaypointLocationByName(Player owner, String waypointName, Location location);

    Promise<BusinessResult<Waypoint, BusinessFailure>> updateWaypointIconById(Player owner, long waypointId, Material icon);

    Promise<BusinessResult<Waypoint, BusinessFailure>> deleteWaypoint(Player owner, long waypointId);

    Promise<BusinessResult<WaypointSharingRequest, BusinessFailure>> sendWaypointSharingRequest(Player owner, String waypointName, Player target);

    Promise<BusinessResult<WaypointShare, BusinessFailure>> acceptWaypointSharingRequest(Player player, UUID requestId);

    Promise<BusinessResult<WaypointSharingRequest, BusinessFailure>> cancelWaypointSharingRequest(Player player, UUID requestId);

    Promise<BusinessResult<WaypointShare, BusinessFailure>> unshareWaypointByOwner(Player owner, long waypointId, UUID targetId);

    Promise<BusinessResult<WaypointShare, BusinessFailure>> unshareWaypointBySharedWith(Player sharedWith, long waypointId);

    Promise<List<WaypointShare>> getSharedWaypoints(Player player);

    Promise<List<WaypointShare>> getSharedWith(Player owner, long waypointId);
}
