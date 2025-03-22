package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.minewaypoints.business.settings.WaypointSettings;
import org.bukkit.configuration.file.FileConfiguration;

public class WaypointSettingsLoader {

    public WaypointSettings loadWaypointSettings(FileConfiguration config) throws ConfigurationException {

        int waypointLimit = config.getInt("waypoint-limit", 50);

        if(waypointLimit != -1 && waypointLimit <= 0) {
            throw new ConfigurationException("waypoint-limit must be a strictly positive integer or -1 (none)");
        }

        return new WaypointSettings(waypointLimit);
    }
}
