package com.github.syr0ws.minewaypoints.business.service.impl;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointUserService;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;

import java.util.Optional;
import java.util.UUID;

public class SimpleBusinessWaypointUserService implements BusinessWaypointUserService {

    private final WaypointUserDAO waypointUserDAO;

    public SimpleBusinessWaypointUserService(WaypointUserDAO waypointUserDAO) {
        Validate.notNull(waypointUserDAO, "waypointUserDAO cannot be null");
        this.waypointUserDAO = waypointUserDAO;
    }

    @Override
    public WaypointOwner createDataIfNotExists(UUID playerId, String playerName) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        Validate.notEmpty(playerName, "playerName cannot be null or empty");

        Optional<WaypointOwnerEntity> optional = this.waypointUserDAO.findOwnerById(playerId);

        if(optional.isPresent()) {
            return optional.get();
        }

        return this.waypointUserDAO.createUser(playerId, playerName);
    }

    @Override
    public boolean hasData(UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        return this.waypointUserDAO.userExists(playerId);
    }

    @Override
    public Optional<WaypointOwner> getWaypointOwner(UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        return this.waypointUserDAO.findOwnerById(playerId).map(owner -> owner);
    }
}
