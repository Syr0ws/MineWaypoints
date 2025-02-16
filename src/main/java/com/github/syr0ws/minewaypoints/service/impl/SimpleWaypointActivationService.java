package com.github.syr0ws.minewaypoints.service.impl;

import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.cache.impl.SimpleWaypointVisibleCache;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.listener.WaypointActivationListener;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.service.WaypointActivationService;
import com.github.syr0ws.minewaypoints.service.util.WaypointEnums;
import com.github.syr0ws.minewaypoints.util.ConfigUtil;
import com.github.syr0ws.minewaypoints.util.Direction;
import com.github.syr0ws.minewaypoints.util.DirectionUtil;
import com.github.syr0ws.minewaypoints.util.placeholder.CustomPlaceholder;
import com.github.syr0ws.minewaypoints.util.placeholder.PlaceholderUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SimpleWaypointActivationService implements WaypointActivationService {

    private final Plugin plugin;
    private final WaypointDAO waypointDAO;
    private final WaypointVisibleCache cache;

    public SimpleWaypointActivationService(Plugin plugin, WaypointDAO waypointDAO) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointDAO, "waypointDAO cannot be null");

        this.plugin = plugin;
        this.waypointDAO = waypointDAO;
        this.cache = new SimpleWaypointVisibleCache();

        PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new WaypointActivationListener(plugin, this), plugin);

        this.startWaypointDisplayTask();
    }

    @Override
    public Promise<WaypointEnums.WaypointActivationStatus> activateWaypoint(Player player, long waypointId) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<>((resolve, reject) -> {
            WaypointEnums.WaypointActivationStatus status = this.activateWaypointInternal(player, waypointId);
            resolve.accept(status);
        });
    }

    @Override
    public Promise<Void> deactivateWaypoint(Player player, long waypointId) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<>((resolve, reject) -> {
            this.deactivateWaypointInternal(player, waypointId);
            resolve.accept(null);
        });
    }

    @Override
    public Promise<WaypointEnums.WaypointToggleStatus> toggleWaypoint(Player player, long waypointId) {
        Validate.notNull(player, "player cannot be null");

        UUID playerId = player.getUniqueId();

        return new Promise<>((resolve, reject) -> {

            boolean isActivated = this.waypointDAO.isActivated(playerId, waypointId);

            if(isActivated) {
                this.deactivateWaypointInternal(player, waypointId);
                resolve.accept(WaypointEnums.WaypointToggleStatus.DEACTIVATED);
            } else {
                WaypointEnums.WaypointActivationStatus status = this.activateWaypointInternal(player, waypointId);
                resolve.accept(WaypointEnums.WaypointToggleStatus.valueOf(status.name()));
            }
        });
    }

    @Override
    public Promise<Boolean> isActivated(Player player, Waypoint waypoint) {
        return null;
    }

    @Override
    public Promise<Optional<Waypoint>> getActivatedWaypoint(Player player, String world) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(world, "world cannot be null");

        UUID playerId = player.getUniqueId();

        return new Promise<>((resolve, reject) -> {
            Optional<Waypoint> optional = this.waypointDAO.findActivatedWaypoint(playerId, world)
                    .map(entity -> entity);
            resolve.accept(optional);
        });
    }

    @Override
    public void showWaypoint(Player player, Waypoint waypoint) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(waypoint, "waypoint cannot be null");

        this.cache.showWaypoint(player, waypoint);
    }

    @Override
    public void hideWaypoint(Player player) {
        Validate.notNull(player, "player cannot be null");

        this.cache.hideWaypoint(player);
    }

    @Override
    public void hideAll() {
        this.cache.hideAll();
    }

    private WaypointEnums.WaypointActivationStatus activateWaypointInternal(Player player, long waypointId) throws WaypointDataException {

        UUID playerId = player.getUniqueId();

        // Retrieving the waypoint.
        Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

        if(optional.isEmpty()) {
            return WaypointEnums.WaypointActivationStatus.WAYPOINT_NOT_FOUND;
        }

        WaypointEntity waypoint = optional.get();

        // A player must have access to the waypoint to activate it.
        if(!this.waypointDAO.hasAccessToWaypoint(playerId, waypointId)) {
            return WaypointEnums.WaypointActivationStatus.NO_WAYPOINT_ACCESS;
        }

        // At most one waypoint can be activated for a player in a world.
        // Here, we deactivate any other activated waypoint for the player in the given world.
        this.waypointDAO.deactivateWaypoint(playerId, waypoint.getLocation().getWorld());

        // Activating the waypoint.
        this.waypointDAO.activateWaypoint(playerId, waypointId);

        String playerWorld = player.getWorld().getName();
        String waypointWorld = waypoint.getLocation().getWorld();

        if(playerWorld.equals(waypointWorld)) {
            this.showWaypoint(player, waypoint);
        }

        return WaypointEnums.WaypointActivationStatus.ACTIVATED;
    }

    private void deactivateWaypointInternal(Player player, long waypointId) throws WaypointDataException {

        UUID playerId = player.getUniqueId();

        Optional<WaypointEntity> optional = this.waypointDAO.findWaypoint(waypointId);

        if(optional.isEmpty()) {
            return;
        }

        WaypointEntity waypoint = optional.get();

        this.waypointDAO.deactivateWaypoint(playerId, waypointId);

        // If the deactivated waypoint is the one currently shown, hiding it.
        String playerWorld = player.getWorld().getName();
        String waypointWorld = waypoint.getLocation().getWorld();

        if(playerWorld.equals(waypointWorld)) {
            this.hideWaypoint(player);
        }
    }

    private void startWaypointDisplayTask() {

        FileConfiguration config = this.plugin.getConfig();
        long displayFrequency = config.getLong("waypoint-display-frequency", 20);

        WaypointDisplayTask task = new WaypointDisplayTask();
        task.runTaskTimer(this.plugin, 0L, displayFrequency);
    }

    private class WaypointDisplayTask extends BukkitRunnable {

        @Override
        public void run() {
            SimpleWaypointActivationService.this.cache.getPlayerWithVisibleWaypoints().forEach(((player, waypoint) -> {

                Location currentLocation = player.getLocation();
                Location waypointLocation = waypoint.getLocation().toLocation();

                FileConfiguration config = SimpleWaypointActivationService.this.plugin.getConfig();
                ConfigurationSection directionSection = config.getConfigurationSection("direction");

                Direction direction = DirectionUtil.getDirectionTo(currentLocation, waypointLocation);
                String directionIcon = ConfigUtil.getDirectionIcon(direction, directionSection);

                int distance = (int) currentLocation.distance(waypointLocation);

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(SimpleWaypointActivationService.this.plugin, waypoint);
                placeholders.put(CustomPlaceholder.WAYPOINT_DIRECTION, directionIcon);
                placeholders.put(CustomPlaceholder.WAYPOINT_DISTANCE, String.valueOf(distance));

                MessageUtil.sendActionBar(player, config, "waypoint-display-actionbar", placeholders);
            }));
        }
    }
}
