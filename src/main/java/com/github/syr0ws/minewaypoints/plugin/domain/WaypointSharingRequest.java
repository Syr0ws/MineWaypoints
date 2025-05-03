package com.github.syr0ws.minewaypoints.plugin.domain;

import java.util.UUID;

public record WaypointSharingRequest(UUID requestId, Waypoint waypoint, WaypointUser target, long createdAt) {
}
