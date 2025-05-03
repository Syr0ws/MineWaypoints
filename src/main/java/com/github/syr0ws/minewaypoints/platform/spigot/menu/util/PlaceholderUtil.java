package com.github.syr0ws.minewaypoints.platform.spigot.menu.util;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.transform.placeholder.PlaceholderManager;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.placeholder.WaypointPlaceholderEnum;
import com.github.syr0ws.minewaypoints.platform.spigot.menu.placeholder.WaypointSharePlaceholderEnum;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class PlaceholderUtil {

    public static void addWaypointPlaceholders(PlaceholderManager manager, Plugin plugin) {
        Validate.notNull(manager, "manager cannot be null");
        Validate.notNull(plugin, "plugin cannot be null");

        Arrays.stream(WaypointPlaceholderEnum.values())
                .map(placeholder -> placeholder.get(plugin))
                .forEach(manager::addPlaceholder);
    }

    public static void addWaypointSharePlaceholders(PlaceholderManager manager, Plugin plugin) {
        Validate.notNull(manager, "manager cannot be null");
        Validate.notNull(plugin, "plugin cannot be null");

        Arrays.stream(WaypointSharePlaceholderEnum.values())
                .map(placeholder -> placeholder.get(plugin))
                .forEach(manager::addPlaceholder);
    }
}
