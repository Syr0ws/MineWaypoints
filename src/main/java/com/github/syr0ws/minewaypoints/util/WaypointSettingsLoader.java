package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.minewaypoints.settings.WaypointLimitPermission;
import com.github.syr0ws.minewaypoints.settings.WaypointSettings;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class WaypointSettingsLoader {

    private static final String WAYPOINTS_CREATE_LIMITS_KEY = "waypoint-create-limits";

    public WaypointSettings loadWaypointSettings(FileConfiguration config) throws ConfigurationException {

        List<WaypointLimitPermission> createLimitPermissions = this.loadWaypointCreateLimitPermissions(config);
        Set<String> forbiddenWorlds = new HashSet<>(config.getStringList("forbidden-worlds"));

        return new WaypointSettings(forbiddenWorlds, createLimitPermissions);
    }

    private List<WaypointLimitPermission> loadWaypointCreateLimitPermissions(FileConfiguration config) throws ConfigurationException {

        List<WaypointLimitPermission> permissions = new ArrayList<>();
        List<Map<?,?>> mapList = config.getMapList("waypoint-create-limits");

        for(Map<?,?> map : mapList) {

            if(!map.containsKey("permission") || !(map.get("permission") instanceof String permission)) {
                throw new ConfigurationException("Missing or invalid key 'permission' at '%s'".formatted(WAYPOINTS_CREATE_LIMITS_KEY));
            }

            if(!map.containsKey("limit") || !(map.get("limit") instanceof Integer limit)) {
                throw new ConfigurationException("Missing or invalid key 'limit' at '%s'".formatted(WAYPOINTS_CREATE_LIMITS_KEY));
            }

            if(limit <= 0 && limit != -1) {
                throw new ConfigurationException("Invalid limit %d at '%s': limit must be strictly positive or -1".formatted(limit, WAYPOINTS_CREATE_LIMITS_KEY));
            }

            permissions.add(new WaypointLimitPermission(permission, limit == -1 ? Integer.MAX_VALUE : limit));
        }

        return permissions;
    }
}
