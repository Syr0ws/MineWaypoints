package com.github.syr0ws.minewaypoints.platform.impl;

import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.config.ConfigUtil;
import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.failure.processor.WaypointFailureProcessor;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointService;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleBukkitWaypointService implements BukkitWaypointService {

    private final Plugin plugin;
    private final BusinessWaypointService waypointService;

    public SimpleBukkitWaypointService(Plugin plugin, BusinessWaypointService waypointService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public Promise<BusinessResult<Waypoint, ?>> createWaypoint(Player owner, String name, Material icon, Location location) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(name, "name cannot be null or empty");
        Validate.notNull(location, "location cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<>((resolve, reject) -> {

            Material waypointIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            BusinessResult<Waypoint, ?> result = this.waypointService.createWaypoint(ownerId, name, waypointIcon.name(), location)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

            resolve.accept(result);
        });
    }

    @Override
    public Promise<BusinessResult<Waypoint, ?>> updateWaypointNameByName(Player owner, String waypointName, String newName) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notEmpty(newName, "newName cannot be null or empty");

        UUID ownerId = owner.getUniqueId();

        return new Promise<>((resolve, reject) -> {

            BusinessResult<Waypoint, ?> result = this.waypointService.updateWaypointNameByName(ownerId, waypointName, newName)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

            resolve.accept(result);
        });
    }

    @Override
    public Promise<BusinessResult<Waypoint, ?>> updateWaypointIconById(Player owner, long waypointId, Material icon) {
        Validate.notNull(owner, "owner cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<>((resolve, reject) -> {

            Material waypointIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            BusinessResult<Waypoint, ?> result = this.waypointService.updateWaypointIconById(ownerId, waypointId, waypointIcon.name())
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

            resolve.accept(result);
        });
    }

    @Override
    public Promise<BusinessResult<Void, ?>> deleteWaypoint(Player owner, long waypointId) {
        Validate.notNull(owner, "owner cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<>((resolve, reject) -> {

            BusinessResult<Void, ?> result = this.waypointService.deleteWaypoint(ownerId, waypointId)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

            resolve.accept(result);
        });
    }

    @Override
    public Promise<BusinessResult<WaypointShare, ?>> shareWaypoint(Player owner, long waypointId, String targetName) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(targetName, "targetName cannot be null or empty");

        return new Promise<>((resolve, reject) -> {

            BusinessResult<WaypointShare, ?> result = this.waypointService.shareWaypoint(owner.getUniqueId(), waypointId, targetName)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

            resolve.accept(result);
        });
    }

    @Override
    public Promise<BusinessResult<Void, ?>> unshareWaypointByOwner(Player owner, long waypointId, String targetName) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(targetName, "targetName cannot be null or empty");

        return new Promise<>((resolve, reject) -> {

            BusinessResult<Void, ?> result = this.waypointService.unshareWaypointByOwner(owner.getUniqueId(), waypointId, targetName)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

            resolve.accept(result);
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWaypoints(Player player) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<>((resolve, reject) -> {
            this.waypointService.getSharedWaypoints(player.getUniqueId())
                    .onSuccess(resolve)
                    .onFailure(ignored -> resolve.accept(new ArrayList<>()));
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWith(Player owner, long waypointId) {
        Validate.notNull(owner, "owner cannot be null");

        return new Promise<>((resolve, reject) -> {
            this.waypointService.getSharedWith(waypointId)
                    .onSuccess(resolve)
                    .onFailure(ignored -> resolve.accept(new ArrayList<>()));
        });
    }

    private Material getDefaultWaypointIcon() {
        try {
            return ConfigUtil.getMaterial(this.plugin.getConfig(), "default-waypoint-icon");
        } catch (ConfigurationException exception) {
            throw new IllegalArgumentException("Cannot assign waypoint icon", exception);
        }
    }
}
