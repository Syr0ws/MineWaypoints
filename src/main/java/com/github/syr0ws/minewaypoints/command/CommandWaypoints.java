package com.github.syr0ws.minewaypoints.command;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.internal.util.TextUtil;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.util.Callback;
import com.github.syr0ws.minewaypoints.util.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class CommandWaypoints implements CommandExecutor {

    private final Plugin plugin;
    private final InventoryService inventoryService;
    private final WaypointService waypointService;

    public CommandWaypoints(Plugin plugin, InventoryService inventoryService, WaypointService waypointService) {

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if (inventoryService == null) {
            throw new IllegalArgumentException("inventoryService cannot be null");
        }

        if (waypointService == null) {
            throw new IllegalArgumentException("waypointService cannot be null");
        }

        this.plugin = plugin;
        this.inventoryService = inventoryService;
        this.waypointService = waypointService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration config = this.plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("command-waypoints");

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }

        // Checking that the player has the permission to execute the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS.getName())) {
            String message = section.getString("no-permission", "");
            player.sendMessage(TextUtil.parseColors(message));
            return true;
        }

        // Command /waypoints
        if (args.length == 0) {
            this.showWaypoints(player, section);
            return true;
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("create")) {
                this.createWaypoint(player, section, args[1]);
                return true;
            }
        }

        return true;
    }

    private void showWaypoints(Player player, ConfigurationSection section) {

        String message = section.getString("show-waypoints", "");
        player.sendMessage(TextUtil.parseColors(message));

        InventoryViewer viewer = this.inventoryService.getInventoryViewer(player);

        this.inventoryService.getProvider(WaypointsMenuDescriptor.MENU_ID).ifPresent(provider -> {
            CraftVentory inventory = provider.createInventory(this.inventoryService, player);
            viewer.getViewManager().openView(inventory, true);
        });
    }

    private void createWaypoint(Player player, ConfigurationSection section, String waypointName) {

        ConfigurationSection createSection = section.getConfigurationSection("create");

        this.waypointService.createWaypointAsync(player.getUniqueId(), waypointName, null, player.getLocation(), new Callback<>() {

            @Override
            public void onSuccess(Waypoint value) {
                String message = createSection.getString("success", "");
                player.sendMessage(TextUtil.parseColors(message));
            }

            @Override
            public void onError(Throwable throwable) {
                CommandWaypoints.this.plugin.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);

                String message = createSection.getString("error", "");
                player.sendMessage(TextUtil.parseColors(message));
            }
        });
    }
}
