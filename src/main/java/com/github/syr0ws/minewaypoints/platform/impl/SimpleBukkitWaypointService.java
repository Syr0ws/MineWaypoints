package com.github.syr0ws.minewaypoints.platform.impl;

import com.github.syr0ws.crafter.config.ConfigUtil;
import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.failure.processor.WaypointFailureProcessor;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointService;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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
    public Promise<Waypoint> createWaypoint(Player owner, String name, Material icon, Location location) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(name, "name cannot be null or empty");
        Validate.notNull(location, "location cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<Waypoint>((resolve, reject) -> {

            Material waypointIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            this.waypointService.createWaypoint(ownerId, name, waypointIcon.name(), location)
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while creating the waypoint", throwable);
            this.sendErrorMessage(owner);
        });
    }

    @Override
    public Promise<Waypoint> updateWaypointNameByName(Player owner, String waypointName, String newName) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notEmpty(newName, "newName cannot be null or empty");

        UUID ownerId = owner.getUniqueId();

        return new Promise<Waypoint>((resolve, reject) -> {
            this.waypointService.updateWaypointNameByName(ownerId, waypointName, newName)
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));
        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while renaming the waypoint", throwable);
            this.sendErrorMessage(owner);
        });
    }

    @Override
    public Promise<Waypoint> updateWaypointIconById(Player owner, long waypointId, Material icon) {
        Validate.notNull(owner, "owner cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<Waypoint>((resolve, reject) -> {

            Material waypointIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            this.waypointService.updateWaypointIconById(ownerId, waypointId, waypointIcon.name())
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));
        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while updating the waypoint icon", throwable);
            this.sendErrorMessage(owner);
        });
    }

    @Override
    public Promise<Void> deleteWaypoint(Player owner, long waypointId) {
        Validate.notNull(owner, "owner cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<Void>((resolve, reject) -> {
            this.waypointService.deleteWaypoint(ownerId, waypointId)
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));
        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while updating the waypoint icon", throwable);
            this.sendErrorMessage(owner);
        });
    }

    @Override
    public Promise<WaypointShare> shareWaypoint(Player owner, long waypointId, String targetName) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(targetName, "targetName cannot be null or empty");

        return new Promise<WaypointShare>((resolve, reject) -> {
            this.waypointService.shareWaypoint(owner.getUniqueId(), waypointId, targetName)
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));
        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while sharing the waypoint", throwable);
            this.sendErrorMessage(owner);
        });
    }

    @Override
    public Promise<Void> unshareWaypointByOwner(Player owner, long waypointId, String targetName) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(targetName, "targetName cannot be null or empty");

        return new Promise<Void>((resolve, reject) -> {
            this.waypointService.unshareWaypointByOwner(owner.getUniqueId(), waypointId, targetName)
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));
        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while unsharing the waypoint", throwable);
            this.sendErrorMessage(owner);
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWaypoints(Player player) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<List<WaypointShare>>((resolve, reject) -> {
            this.waypointService.getSharedWaypoints(player.getUniqueId())
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, player).process(failure));
        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while retrieving user' shared waypoints", throwable);
            this.sendErrorMessage(player);
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWith(Player owner, long waypointId) {
        Validate.notNull(owner, "owner cannot be null");

        return new Promise<List<WaypointShare>>((resolve, reject) -> {
            this.waypointService.getSharedWith(waypointId)
                    .onSuccess(resolve)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));
        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while retrieving the users the waypoint is shared with", throwable);
            this.sendErrorMessage(owner);
        });
    }

    private void sendErrorMessage(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        MessageUtil.sendMessage(player, config, "errors.generic");
    }

    private Material getDefaultWaypointIcon() {
        try {
            return ConfigUtil.getMaterial(this.plugin.getConfig(), "default-waypoint-icon");
        } catch (ConfigurationException exception) {
            throw new IllegalArgumentException("Cannot assign waypoint icon", exception);
        }
    }
}
