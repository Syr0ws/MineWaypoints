package com.github.syr0ws.minewaypoints.business.service;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;

import java.util.Optional;
import java.util.UUID;

public interface BusinessWaypointUserService {

    WaypointOwner loadData(UUID playerId) throws WaypointDataException;

    void unloadData(UUID playerId) throws WaypointDataException;

    WaypointOwner createDataIfNotExists(UUID playerId, String playerName) throws WaypointDataException;

    Optional<WaypointOwner> getWaypointOwner(UUID playerId) throws WaypointDataException;
}
