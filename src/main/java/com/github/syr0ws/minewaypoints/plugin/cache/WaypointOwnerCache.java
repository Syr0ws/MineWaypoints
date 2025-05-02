package com.github.syr0ws.minewaypoints.plugin.cache;

import com.github.syr0ws.minewaypoints.plugin.domain.WaypointOwner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaypointOwnerCache<T extends WaypointOwner> {

    void addData(T owner);

    void removeData(UUID ownerId);

    boolean hasData(UUID ownerId);

    Optional<T> getOwner(UUID ownerId);

    List<T> getOwners();
}
