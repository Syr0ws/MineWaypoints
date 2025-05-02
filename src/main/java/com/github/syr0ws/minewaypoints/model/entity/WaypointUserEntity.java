package com.github.syr0ws.minewaypoints.model.entity;

import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.util.UUID;

public class WaypointUserEntity implements WaypointUser {

    private final UUID id;
    private final String name;

    public WaypointUserEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
