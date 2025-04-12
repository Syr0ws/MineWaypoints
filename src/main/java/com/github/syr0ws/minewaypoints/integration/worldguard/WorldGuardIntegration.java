package com.github.syr0ws.minewaypoints.integration.worldguard;

import com.github.syr0ws.minewaypoints.integration.Integration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class WorldGuardIntegration extends Integration {

    public WorldGuardIntegration(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        Plugin plugin = super.getPlugin();
        super.registerListener(new WorldGuardListener(plugin));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean canBeEnabled() {
        FileConfiguration config = super.getPlugin().getConfig();
        return config.getBoolean("worldguard.enabled", false) && Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    }

    @Override
    public String getName() {
        return "WorldGuard";
    }
}
