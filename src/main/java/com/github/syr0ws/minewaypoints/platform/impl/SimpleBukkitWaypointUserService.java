package com.github.syr0ws.minewaypoints.platform.impl;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.business.service.BusinessWaypointUserService;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointOwner;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointUserService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.logging.Level;

public class SimpleBukkitWaypointUserService implements BukkitWaypointUserService {

    private final Plugin plugin;
    private final BusinessWaypointUserService waypointUserService;

    public SimpleBukkitWaypointUserService(Plugin plugin, BusinessWaypointUserService waypointUserService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointUserService, "waypointUserService cannot be null");

        this.plugin = plugin;
        this.waypointUserService = waypointUserService;
    }

    @Override
    public Promise<WaypointOwner> loadData(Player player) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<WaypointOwner>((resolve, reject) -> {

            this.waypointUserService.createDataIfNotExists(player.getUniqueId(), player.getName());
            WaypointOwner owner = this.waypointUserService.loadData(player.getUniqueId());

            resolve.accept(owner);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while loading player data.", throwable);
        });
    }

    @Override
    public Promise<Void> unloadData(Player player) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<Void>((resolve, reject) -> {

            this.waypointUserService.unloadData(player.getUniqueId());
            resolve.accept(null);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while unloading player data.", throwable);
        });
    }

    @Override
    public Promise<WaypointOwner> createDataIfNotExists(Player player) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<WaypointOwner>((resolve, reject) -> {

            WaypointOwner owner = this.waypointUserService.createDataIfNotExists(player.getUniqueId(), player.getName());
            resolve.accept(owner);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while creating player data.", throwable);
        });
    }

    @Override
    public Promise<Optional<WaypointOwner>> getWaypointOwner(Player player) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<Optional<WaypointOwner>>((resolve, reject) -> {

            Optional<WaypointOwner> optional = this.waypointUserService.getWaypointOwner(player.getUniqueId());
            resolve.accept(optional);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while retrieving player data.", throwable);
        });
    }
}
