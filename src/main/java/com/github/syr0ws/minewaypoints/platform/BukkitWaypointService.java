package com.github.syr0ws.minewaypoints.platform;

import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public interface BukkitWaypointService {

    Promise<BusinessResult<Waypoint, ?>> createWaypoint(Player owner, String name, Material icon, Location location);

    Promise<BusinessResult<Waypoint, ?>> updateWaypointNameByName(Player owner, String waypointName, String newName);

    Promise<BusinessResult<Waypoint, ?>> updateWaypointIconById(Player owner, long waypointId, Material icon);

    Promise<BusinessResult<Void, ?>> deleteWaypoint(Player owner, long waypointId);

    Promise<BusinessResult<WaypointShare, ?>> shareWaypoint(Player owner, long waypointId, String targetName);

    Promise<BusinessResult<Void, ?>> unshareWaypointByOwner(Player owner, long waypointId, String targetName);

    Promise<List<WaypointShare>> getSharedWaypoints(Player player);

    Promise<List<WaypointShare>> getSharedWith(Player owner, long waypointId);
}
