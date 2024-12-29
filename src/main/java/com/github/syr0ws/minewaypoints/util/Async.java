package com.github.syr0ws.minewaypoints.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Async {

    public static void runAsync(Plugin plugin, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static void runSync(Plugin plugin, Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
