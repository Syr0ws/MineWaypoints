package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.WaypointSharingRequest;

import java.util.Optional;
import java.util.UUID;

public interface WaypointSharingRequestCache {

    void addSharingRequest(WaypointSharingRequest request);

    void removeSharingRequest(UUID requestId);

    boolean hasSharingRequest(UUID requestId);

    Optional<WaypointSharingRequest> getSharingRequest(UUID requestId);
}
