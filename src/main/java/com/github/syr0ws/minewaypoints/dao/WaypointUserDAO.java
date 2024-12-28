package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.util.UUID;

public interface WaypointUserDAO {

    WaypointUser createUser(UUID userId, String name) throws WaypointDataException;

    boolean userExists(UUID userId) throws WaypointDataException;

    WaypointUser findUser(UUID userId) throws WaypointDataException;
}
