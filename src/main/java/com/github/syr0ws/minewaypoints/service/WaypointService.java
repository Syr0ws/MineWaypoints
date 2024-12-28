package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.util.Callback;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface WaypointService {

    List<Waypoint> loadWaypoints(Player player) throws WaypointDataException;

    void loadWaypointsAsync(Player player, Callback<List<Waypoint>> callback);

    WaypointShare shareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException;

    void shareWaypointAsync(WaypointUser user, long waypointId, Callback<WaypointShare> callback);

    void unshareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException;

    void unshareWaypointAsync(WaypointUser user, long waypointId, Callback<Void> callback);

    List<Waypoint> getWaypoints(WaypointUser user);

    Map<WaypointShare, Waypoint> getSharedWaypoints(WaypointUser user);
}
