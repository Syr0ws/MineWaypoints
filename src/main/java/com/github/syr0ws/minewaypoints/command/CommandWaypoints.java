package com.github.syr0ws.minewaypoints.command;

import com.github.syr0ws.craftventory.internal.util.TextUtil;
import com.github.syr0ws.minewaypoints.util.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandWaypoints implements CommandExecutor {

    private final Plugin plugin;

    public CommandWaypoints(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration config = this.plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("command-waypoints");

        if(!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }

        if(!player.hasPermission(Permission.COMMAND_WAYPOINTS.getName())) {
            String message = section.getString("no-permission", "");
            player.sendMessage(TextUtil.parseColors(message));
            return true;
        }

        String message = section.getString("show-waypoints", "");
        player.sendMessage(TextUtil.parseColors(message));

        return true;
    }
}
