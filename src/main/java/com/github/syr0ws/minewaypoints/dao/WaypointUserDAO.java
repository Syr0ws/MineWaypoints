package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface WaypointUserDAO {

    WaypointOwnerEntity createUser(UUID userId, String name) throws WaypointDataException;

    boolean userExists(UUID userId) throws WaypointDataException;

    WaypointOwnerEntity findOwner(UUID userId) throws WaypointDataException;

    Optional<WaypointUserEntity> findUser(UUID userId) throws WaypointDataException;
}
