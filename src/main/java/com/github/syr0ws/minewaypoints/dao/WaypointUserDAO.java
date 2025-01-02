package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointUserModel;

import java.util.UUID;

public interface WaypointUserDAO {

    WaypointUserModel createUser(UUID userId, String name) throws WaypointDataException;

    boolean userExists(UUID userId) throws WaypointDataException;

    WaypointUserModel findUser(UUID userId) throws WaypointDataException;
}
