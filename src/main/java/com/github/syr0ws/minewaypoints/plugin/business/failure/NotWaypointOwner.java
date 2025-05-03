package com.github.syr0ws.minewaypoints.plugin.business.failure;

import com.github.syr0ws.crafter.business.BusinessFailure;

public record NotWaypointOwner(long waypointId) implements BusinessFailure {

}
