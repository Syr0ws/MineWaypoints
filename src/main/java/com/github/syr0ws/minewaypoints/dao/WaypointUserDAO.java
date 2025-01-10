package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;

import java.util.UUID;

public interface WaypointUserDAO {

    WaypointOwnerEntity createUser(UUID userId, String name) throws WaypointDataException;

    boolean userExists(UUID userId) throws WaypointDataException;

    WaypointOwnerEntity findOwner(UUID userId) throws WaypointDataException;
}
