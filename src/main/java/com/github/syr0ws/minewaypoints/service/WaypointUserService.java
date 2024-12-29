package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.util.Callback;

import java.util.UUID;

public interface WaypointUserService {

    WaypointUser createData(UUID userId, String name) throws WaypointDataException;

    void createDataAsync(UUID userId, String name, Callback<WaypointUser> callback);

    WaypointUser loadData(UUID userId) throws WaypointDataException;

    void loadDataAsync(UUID userId, Callback<WaypointUser> callback);

    void unloadData(UUID userId);

    boolean hasData(UUID userId) throws WaypointDataException;

    boolean hasLoadedData(UUID userId);

    void hasDataAsync(UUID userId, Callback<Boolean> callback);

    WaypointUser getWaypointUser(UUID userId);
}
