package com.github.syr0ws.minewaypoints.platform.spigot.service.impl;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.business.service.BusinessWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.spigot.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.platform.spigot.service.BukkitWaypointActivationService;
import com.github.syr0ws.minewaypoints.platform.spigot.processor.WaypointFailureProcessor;
import com.github.syr0ws.minewaypoints.platform.spigot.util.WaypointDisplayTask;
import com.github.syr0ws.minewaypoints.platform.spigot.util.placeholder.PlaceholderUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class SimpleBukkitWaypointActivationService implements BukkitWaypointActivationService {

    private final Plugin plugin;
    private final BusinessWaypointActivationService service;
    private final WaypointVisibleCache cache;

    public SimpleBukkitWaypointActivationService(Plugin plugin, BusinessWaypointActivationService service, WaypointVisibleCache cache) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(service, "service cannot be null");
        Validate.notNull(cache, "cache cannot be null");

        this.plugin = plugin;
        this.service = service;
        this.cache = cache;

        WaypointDisplayTask task = new WaypointDisplayTask(plugin, cache);
        task.start();
    }

    @Override
    public Promise<BusinessResult<Waypoint, BusinessFailure>> activateWaypoint(Player player, long waypointId) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<BusinessResult<Waypoint, BusinessFailure>>(((resolve, reject) -> {

            BusinessResult<Waypoint, BusinessFailure> result = this.service.activateWaypoint(
                    player.getUniqueId(), waypointId);

            resolve.accept(result);

        })).then(result -> {

            result.onSuccess(waypoint -> {
                this.onWaypointActivated(player, waypoint);
            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, player).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while activating the waypoint", throwable);
            MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<Waypoint, BusinessFailure>> deactivateWaypoint(Player player, long waypointId) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<BusinessResult<Waypoint, BusinessFailure>>(((resolve, reject) -> {

            BusinessResult<Waypoint, BusinessFailure> result = this.service.deactivateWaypoint(
                    player.getUniqueId(), waypointId);

            resolve.accept(result);

        })).then(result -> {

            result.onSuccess(waypoint -> {
                this.onWaypointDeactivated(player, waypoint);
            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, player).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while deactivating the waypoint", throwable);
            MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<Optional<Waypoint>> getActivatedWaypoint(UUID playerId, World world) {
        Validate.notNull(playerId, "playerId cannot be null");
        Validate.notNull(world, "world cannot be null");

        return new Promise<Optional<Waypoint>>((resolve, reject) -> {

            Optional<Waypoint> optional = this.service.getActivatedWaypoint(playerId, world.getName());
            resolve.accept(optional);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while retrieving the activated waypoint", throwable);
        });
    }

    @Override
    public Promise<Set<Long>> getActivatedWaypointIds(UUID playerId) {
        Validate.notNull(playerId, "playerId cannot be null");

        return new Promise<Set<Long>>((resolve, reject) -> {

            Set<Long> waypointIds = this.service.getActivatedWaypointIds(playerId);
            resolve.accept(waypointIds);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while retrieving the activated waypoint", throwable);
        });
    }

    private void onWaypointActivated(Player player, Waypoint waypoint) {

        // Showing the waypoint to the player if it is in the same world.
        String playerWorld = player.getWorld().getName();
        String waypointWorld = waypoint.getLocation().getWorld();

        if (playerWorld.equals(waypointWorld)) {
            this.cache.showWaypoint(player, waypoint);
        }

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
        MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.waypoint.activation.activated", placeholders);
    }

    private void onWaypointDeactivated(Player player, Waypoint waypoint) {

        // If the deactivated waypoint is the one currently shown, hiding it.
        String playerWorld = player.getWorld().getName();
        String waypointWorld = waypoint.getLocation().getWorld();

        if (playerWorld.equals(waypointWorld)) {
            this.cache.hideWaypoint(player);
        }

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
        MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.waypoint.activation.deactivated", placeholders);
    }
}
