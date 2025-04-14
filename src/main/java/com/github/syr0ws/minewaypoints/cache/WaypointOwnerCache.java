package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.WaypointOwner;

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
