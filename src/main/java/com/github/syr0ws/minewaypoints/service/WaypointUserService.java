package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.util.Promise;

import java.util.UUID;

public interface WaypointUserService {

    Promise<WaypointUser> createData(UUID userId, String name);

    Promise<WaypointUser> loadData(UUID userId);

    Promise<Void> unloadData(UUID userId);

    Promise<Boolean> hasData(UUID userId);
}
