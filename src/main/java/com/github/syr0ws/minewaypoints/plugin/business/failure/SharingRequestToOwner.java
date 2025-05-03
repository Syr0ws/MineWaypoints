package com.github.syr0ws.minewaypoints.plugin.business.failure;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointUser;

public record SharingRequestToOwner(Waypoint waypoint, WaypointUser target) implements BusinessFailure {
}
