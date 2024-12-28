package com.github.syr0ws.minewaypoints.dao;

import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.util.UUID;

public interface WaypointUserDAO {

    WaypointUser save(WaypointUser user);

    boolean exists(WaypointUser user);

    WaypointUser getWaypointUser(UUID userId);
}
