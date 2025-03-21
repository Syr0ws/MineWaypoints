package com.github.syr0ws.minewaypoints.model;

import java.util.UUID;

public record WaypointSharingRequest(UUID requestId, Waypoint waypoint, WaypointUser target, long createdAt) {
}
