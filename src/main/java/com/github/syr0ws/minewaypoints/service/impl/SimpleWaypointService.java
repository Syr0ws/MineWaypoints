package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.util.Async;
import com.github.syr0ws.minewaypoints.util.Callback;
import org.bukkit.plugin.Plugin;

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
    public WaypointShare shareWaypoint(WaypointUser user, long waypointId) throws WaypointDataException {

        if(user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        // Updating database.
        WaypointShare share = this.waypointDAO.shareWaypoint(user, waypointId);

        // Updating cache.
        user.shareWaypoint(share);

        return share;
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

        // Updating database.
        this.waypointDAO.unshareWaypoint(user, waypointId);

        // Updating cache.
        user.unshareWaypoint(waypointId);
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
}
