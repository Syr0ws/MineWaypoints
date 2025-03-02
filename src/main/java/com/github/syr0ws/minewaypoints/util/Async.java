package com.github.syr0ws.minewaypoints.util;

import com.github.syr0ws.crafter.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Async {

    public static void runSync(Plugin plugin, Runnable runnable) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(runnable, "runnable cannot be null");

        Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
