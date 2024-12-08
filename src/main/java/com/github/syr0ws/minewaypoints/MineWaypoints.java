package com.github.syr0ws.minewaypoints;

import com.github.syr0ws.minewaypoints.command.CommandWaypoints;
import org.bukkit.plugin.java.JavaPlugin;

public class MineWaypoints extends JavaPlugin {

    @Override
    public void onEnable() {
        this.loadConfiguration();
        this.registerCommands();
    }

    private void loadConfiguration() {
        super.saveDefaultConfig();
    }

    private void registerCommands() {
        super.getCommand("waypoints").setExecutor(new CommandWaypoints(this));
    }
}
