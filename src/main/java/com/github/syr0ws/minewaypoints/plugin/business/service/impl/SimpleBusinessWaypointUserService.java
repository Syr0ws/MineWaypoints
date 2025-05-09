package com.github.syr0ws.minewaypoints.plugin.business.service.impl;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.business.service.BusinessWaypointUserService;
import com.github.syr0ws.minewaypoints.plugin.cache.WaypointOwnerCache;
import com.github.syr0ws.minewaypoints.plugin.persistence.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.plugin.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointOwner;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointOwnerEntity;

import java.util.Optional;
import java.util.UUID;

public class SimpleBusinessWaypointUserService implements BusinessWaypointUserService {

    private final WaypointUserDAO waypointUserDAO;
    private final WaypointOwnerCache<WaypointOwnerEntity> waypointOwnerCache;

    public SimpleBusinessWaypointUserService(WaypointUserDAO waypointUserDAO, WaypointOwnerCache<WaypointOwnerEntity> waypointOwnerCache) {
        Validate.notNull(waypointUserDAO, "waypointUserDAO cannot be null");
        Validate.notNull(waypointOwnerCache, "waypointOwnerCache cannot be null");
        this.waypointUserDAO = waypointUserDAO;
        this.waypointOwnerCache = waypointOwnerCache;
    }

    @Override
    public WaypointOwner loadData(UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");

        WaypointOwnerEntity owner = this.waypointUserDAO.findOwnerById(playerId)
                .orElseThrow(() -> new WaypointDataException("WaypointOwner '%s' not found".formatted(playerId)));

        this.waypointOwnerCache.addData(owner);

        return owner;
    }

    @Override
    public void unloadData(UUID playerId) {
        Validate.notNull(playerId, "playerId cannot be null");
        this.waypointOwnerCache.removeData(playerId);
    }

    @Override
    public WaypointOwner createDataIfNotExists(UUID playerId, String playerName) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        Validate.notEmpty(playerName, "playerName cannot be null or empty");

        Optional<WaypointOwnerEntity> optional = this.waypointUserDAO.findOwnerById(playerId);

        if (optional.isPresent()) {
            return optional.get();
        }

        return this.waypointUserDAO.createUser(playerId, playerName);
    }

    @Override
    public Optional<WaypointOwner> getWaypointOwner(UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        return this.waypointUserDAO.findOwnerById(playerId).map(owner -> owner);
    }
}
