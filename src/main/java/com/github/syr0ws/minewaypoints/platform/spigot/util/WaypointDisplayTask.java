package com.github.syr0ws.minewaypoints.platform.spigot.util;

import com.github.syr0ws.crafter.config.ConfigUtil;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Direction;
import com.github.syr0ws.crafter.util.DirectionUtil;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.platform.spigot.cache.WaypointVisibleCache;
import com.github.syr0ws.minewaypoints.platform.spigot.util.placeholder.CustomPlaceholder;
import com.github.syr0ws.minewaypoints.platform.spigot.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class WaypointDisplayTask implements Runnable {

    private final Plugin plugin;
    private final WaypointVisibleCache cache;

    public WaypointDisplayTask(Plugin plugin, WaypointVisibleCache cache) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(cache, "cache cannot be null");

        this.plugin = plugin;
        this.cache = cache;
    }

    public void start() {

        FileConfiguration config = this.plugin.getConfig();
        long displayFrequency = config.getLong("waypoint-display-frequency", 20);

        Bukkit.getScheduler().runTaskTimer(this.plugin, this, 0L, displayFrequency);
    }

    @Override
    public void run() {

        this.cache.getPlayerWithVisibleWaypoints().forEach(((player, waypoint) -> {

            Location currentLocation = player.getLocation();
            Location waypointLocation = Mapper.toLocation(waypoint.getLocation());

            FileConfiguration config = this.plugin.getConfig();
            ConfigurationSection directionSection = config.getConfigurationSection("direction");

            Direction direction = DirectionUtil.getDirectionTo(currentLocation, waypointLocation);
            String directionIcon = ConfigUtil.getDirectionSymbol(direction, directionSection);

            int distance = (int) currentLocation.distance(waypointLocation);

            Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
            placeholders.put(CustomPlaceholder.WAYPOINT_DIRECTION, directionIcon);
            placeholders.put(CustomPlaceholder.WAYPOINT_DISTANCE, String.valueOf(distance));

            MessageUtil.sendActionBar(player, config, "waypoint-display-actionbar", placeholders);
        }));
    }
}
