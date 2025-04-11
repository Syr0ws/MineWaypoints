package com.github.syr0ws.minewaypoints.business.failure;

import com.github.syr0ws.crafter.business.BusinessFailure;

public record SameWaypointName(String waypointName) implements BusinessFailure {
}
