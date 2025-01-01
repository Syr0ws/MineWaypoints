package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.util.Promise;

import java.util.List;
import java.util.UUID;

public interface WaypointUserService {

    Promise<WaypointUser> createData(UUID userId, String name);

    Promise<WaypointUser> loadData(UUID userId);

    void unloadData(UUID userId);

    Promise<Boolean> hasData(UUID userId);

    boolean hasLoadedData(UUID userId);

    WaypointUser getWaypointUser(UUID userId);

    List<WaypointUser> getWaypointUsers();
}
