package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.util.Async;
import com.github.syr0ws.minewaypoints.util.Callback;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimpleWaypointService implements WaypointService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;

    public SimpleWaypointService(Plugin plugin, WaypointDAO waypointDAO) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(waypointDAO == null) {
            throw new IllegalArgumentException("waypointDAO cannot be null");
        }

        this.plugin = plugin;
        this.waypointDAO = waypointDAO;
    }

    @Override
    public List<Waypoint> loadWaypoints(Player player) throws WaypointDataException {

        if(player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        List<Waypoint> waypoints = this.waypointDAO.findAllWaypoints(player.getUniqueId());
        waypoints.forEach(waypoint -> this.cache.put(waypoint.getId(), waypoint));

        return waypoints;
    }

    @Override
    public void loadWaypointsAsync(Player player, Callback<List<Waypoint>> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                List<Waypoint> waypoints = this.loadWaypoints(player);
                callback.onSuccess(waypoints);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public WaypointShare shareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        Waypoint waypoint = this.cache.getOrDefault(waypointId, null);

        if(waypoint == null) {
            throw new WaypointDataException("Waypoint not found");
        }

        return this.waypointDAO.shareWaypoint(user, waypoint);
    }

    @Override
    public void shareWaypointAsync(WaypointUser user, long waypointId, Callback<WaypointShare> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                WaypointShare share = this.shareWaypoint(user, waypointId);
                callback.onSuccess(share);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void unshareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        Waypoint waypoint = this.cache.getOrDefault(waypointId, null);

        if(waypoint == null) {
            throw new WaypointDataException("Waypoint not found");
        }

        this.waypointDAO.unshareWaypoint(user, waypoint);
    }

    @Override
    public void unshareWaypointAsync(WaypointUser user, long waypointId, Callback<Void> callback) {

        Async.runAsync(this.plugin, () -> {

            try {
                this.unshareWaypoint(user, waypointId);
                callback.onSuccess(null);
            } catch (WaypointDataException exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public List<Waypoint> getWaypoints(WaypointUser user) {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        return user.getWaypoints().stream()
                .map(waypointId -> this.cache.getOrDefault(waypointId, null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Map<WaypointShare, Waypoint> getSharedWaypoints(WaypointUser user) {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        return user.getSharedWaypoints().stream()
                .filter(share -> this.cache.containsKey(share.getWaypointId()))
                .collect(Collectors.toMap(
                        share -> share,
                        share -> this.cache.getOrDefault(share.getWaypointId(), null)
                ));
    }
}
