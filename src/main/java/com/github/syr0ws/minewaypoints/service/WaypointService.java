package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.util.Callback;

public interface WaypointService {

    WaypointShare shareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException;

    void shareWaypointAsync(WaypointUser user, long waypointId, Callback<WaypointShare> callback);

    void unshareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException;

    void unshareWaypointAsync(WaypointUser user, long waypointId, Callback<Void> callback);
}
