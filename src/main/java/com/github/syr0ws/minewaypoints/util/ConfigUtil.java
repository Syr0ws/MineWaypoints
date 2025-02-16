package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.crafter.util.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigUtil {

    public static String getDirectionIcon(Direction direction, ConfigurationSection section) {
        Validate.notNull(direction, "direction cannot be null");
        Validate.notNull(section, "section cannot be null");

        String directionKey = direction.name().toLowerCase().replace("_", "-");

        return section.getString(directionKey, "");
    }
}
