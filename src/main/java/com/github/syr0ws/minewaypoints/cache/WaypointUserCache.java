package com.github.syr0ws.minewaypoints.cache;

import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WaypointUserCache<T extends WaypointUser> {

    void addUser(T user);

    void removeUser(UUID userId);

    boolean hasData(UUID userId);

    Optional<T> getUser(UUID userId);

    Map<UUID, T> getUsers();
}
