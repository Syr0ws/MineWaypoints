package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;

import java.util.UUID;

public interface WaypointUserService {

    Promise<WaypointOwner> createData(UUID userId, String name);

    Promise<WaypointOwner> loadData(UUID userId);

    Promise<Void> unloadData(UUID userId);

    Promise<Boolean> hasData(UUID userId);
}
