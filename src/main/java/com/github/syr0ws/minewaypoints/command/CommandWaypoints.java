package com.github.syr0ws.minewaypoints.command;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.internal.util.TextUtil;
import com.github.syr0ws.minewaypoints.menu.WaypointIconsMenuDescriptor;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
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
    private final InventoryService inventoryService;

    public CommandWaypoints(Plugin plugin, InventoryService inventoryService) {

        if(plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if(inventoryService == null) {
            throw new IllegalArgumentException("inventoryService cannot be null");
        }

        this.plugin = plugin;
        this.inventoryService = inventoryService;
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

        InventoryViewer viewer = this.inventoryService.getInventoryViewer(player);

        this.inventoryService.getProvider(WaypointsMenuDescriptor.MENU_ID).ifPresent(provider -> {
            CraftVentory inventory = provider.createInventory(this.inventoryService, player);
            viewer.getViewManager().openView(inventory, true);
        });

        return true;
    }
}
