package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.minewaypoints.exception.ConfigurationException;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigUtil {

    public static Material getMaterial(ConfigurationSection section, String key) throws ConfigurationException {

        String materialName = section.getString(key, "");
        Material material = Material.matchMaterial(materialName);

        if(material == null) {
            String message = String.format("Invalid material name '%s' at '%s.%s' ", materialName, section.getCurrentPath(), key);
            throw new ConfigurationException(message);
        }

        return material;
    }
}
