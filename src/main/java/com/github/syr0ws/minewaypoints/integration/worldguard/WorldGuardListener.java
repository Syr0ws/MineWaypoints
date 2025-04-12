package com.github.syr0ws.minewaypoints.integration.worldguard;

import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.api.event.AsyncWaypointCreateEvent;
import com.github.syr0ws.minewaypoints.api.event.AsyncWaypointUpdateEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class WorldGuardListener implements Listener {

    private final Plugin plugin;

    public WorldGuardListener(Plugin plugin) {
        Validate.notNull(plugin, "plugin cannot be null");
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWaypointCreate(AsyncWaypointCreateEvent event) {

        if(event.isCancelled()) {
            return;
        }

        boolean isForbiddenLocation = this.isForbiddenLocation(event.getLocation());

        if(isForbiddenLocation) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getOwner(), this.plugin.getConfig(), "worldguard.messages.forbidden-region");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWaypointUpdate(AsyncWaypointUpdateEvent event) {

        if(event.isCancelled()) {
            return;
        }

        Location newLocation = event.getNewLocation();
        Location oldLocation = event.getWaypoint().getLocation().toLocation();

        if(newLocation.equals(oldLocation)) {
            return;
        }

        boolean isForbiddenLocation = this.isForbiddenLocation(event.getNewLocation());

        if(isForbiddenLocation) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getOwner(), this.plugin.getConfig(), "worldguard.messages.forbidden-region");
        }
    }

    private boolean isForbiddenLocation(Location location) {

        FileConfiguration config = this.plugin.getConfig();
        List<String> regionNames = config.getStringList("worldguard.forbidden-regions");

        if(regionNames.isEmpty()) {
            return false;
        }

        World world = BukkitAdapter.adapt(location.getWorld());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(world);

        if(manager == null) {
            return false;
        }

        return regionNames.stream()
                // Checking that the region exists.
                .filter(manager::hasRegion)
                // Retrieving the corresponding region.
                .map(manager::getRegion)
                // Returns true if the location is in any WorldGuard forbidden region.
                .anyMatch(region -> region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
}
