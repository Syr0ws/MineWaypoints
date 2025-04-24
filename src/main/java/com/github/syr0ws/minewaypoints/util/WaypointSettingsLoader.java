package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.minewaypoints.settings.WaypointLimitPermission;
import com.github.syr0ws.minewaypoints.settings.WaypointSettings;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class WaypointSettingsLoader {

    private static final String WAYPOINTS_CREATION_LIMITS_KEY = "waypoint-creation-limits";

    public WaypointSettings loadWaypointSettings(FileConfiguration config) throws ConfigurationException {

        List<WaypointLimitPermission> waypointCreationLimitPermissions = this.loadWaypointCreationLimitPermissions(config);
        Set<String> forbiddenWorlds = new HashSet<>(config.getStringList("forbidden-worlds"));

        return new WaypointSettings(forbiddenWorlds, waypointCreationLimitPermissions);
    }

    private List<WaypointLimitPermission> loadWaypointCreationLimitPermissions(FileConfiguration config) throws ConfigurationException {

        List<WaypointLimitPermission> permissions = new ArrayList<>();
        List<Map<?,?>> mapList = config.getMapList(WAYPOINTS_CREATION_LIMITS_KEY);

        for(Map<?,?> map : mapList) {

            if(!map.containsKey("permission") || !(map.get("permission") instanceof String permission)) {
                throw new ConfigurationException("Missing or invalid key 'permission' at '%s'".formatted(WAYPOINTS_CREATION_LIMITS_KEY));
            }

            if(!map.containsKey("limit") || !(map.get("limit") instanceof Integer limit)) {
                throw new ConfigurationException("Missing or invalid key 'limit' at '%s'".formatted(WAYPOINTS_CREATION_LIMITS_KEY));
            }

            if(limit <= 0 && limit != -1) {
                throw new ConfigurationException("Invalid limit %d at '%s': limit must be strictly positive or -1".formatted(limit, WAYPOINTS_CREATION_LIMITS_KEY));
            }

            permissions.add(new WaypointLimitPermission(permission, limit == -1 ? Integer.MAX_VALUE : limit));
        }

        return permissions;
    }
}
