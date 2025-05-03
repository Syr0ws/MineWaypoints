package com.github.syr0ws.minewaypoints.plugin.business.failure;

import com.github.syr0ws.crafter.business.BusinessFailure;

public record SameWaypointName(String waypointName) implements BusinessFailure {
}
