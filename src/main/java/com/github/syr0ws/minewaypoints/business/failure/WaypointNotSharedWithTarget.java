package com.github.syr0ws.minewaypoints.business.failure;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

public record WaypointNotSharedWithTarget(Waypoint waypoint, WaypointUser target) implements BusinessFailure {

}
