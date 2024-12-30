package com.github.syr0ws.minewaypoints.command;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.service.WaypointUserService;
import com.github.syr0ws.minewaypoints.util.Callback;
import com.github.syr0ws.minewaypoints.util.MessageUtil;
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
    private final WaypointUserService waypointUserService;
    private final WaypointService waypointService;

    public CommandWaypoints(Plugin plugin, InventoryService inventoryService, WaypointUserService waypointUserService, WaypointService waypointService) {

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if (inventoryService == null) {
            throw new IllegalArgumentException("inventoryService cannot be null");
        }

        if(waypointUserService == null) {
            throw new IllegalArgumentException("waypointUserService cannot be null");
        }

        if (waypointService == null) {
            throw new IllegalArgumentException("waypointService cannot be null");
        }

        this.plugin = plugin;
        this.inventoryService = inventoryService;
        this.waypointUserService = waypointUserService;
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
            MessageUtil.sendMessage(player, section, "errors.no-permission");
            return true;
        }

        // Command /waypoints
        if (args.length == 0) {
            this.showWaypoints(player, section);
            return true;
        }

        if (args.length == 2) {

            // Command /waypoints create <name>
            if (args[0].equalsIgnoreCase("create")) {
                this.createWaypoint(player, section, args[1]);
                return true;
            }
        }

        if(args.length == 3) {

            // Command /waypoints rename <old_name> <new_name>
            if(args[0].equalsIgnoreCase("rename")) {
                this.renameWaypoint(player, section, args[1], args[2]);
                return true;
            }
        }

        return true;
    }

    private void showWaypoints(Player player, ConfigurationSection section) {

        MessageUtil.sendMessage(player, section, "show-waypoints");

        InventoryViewer viewer = this.inventoryService.getInventoryViewer(player);

        this.inventoryService.getProvider(WaypointsMenuDescriptor.MENU_ID).ifPresent(provider -> {
            CraftVentory inventory = provider.createInventory(this.inventoryService, player);
            viewer.getViewManager().openView(inventory, true);
        });
    }

    private void createWaypoint(Player player, ConfigurationSection section, String waypointName) {

        ConfigurationSection createSection = section.getConfigurationSection("create");

        // Checking that the player has the required permission to use the command.
        if(!player.hasPermission(Permission.COMMAND_WAYPOINTS_CREATE.getName())) {
            MessageUtil.sendMessage(player, section, "errors.no-permission");
            return;
        }

        WaypointUser user = this.waypointUserService.getWaypointUser(player.getUniqueId());

        // Checking player's data.
        if(user == null) {
            MessageUtil.sendMessage(player, section, "errors.no-data");
            return;
        }

        // Checking that the user does not have a waypoint with the same name.
        if(user.hasWaypointByName(waypointName)) {
            MessageUtil.sendMessage(player, section, "errors.name-already-exists");
            return;
        }

        // Creating the waypoint.
        this.waypointService.createWaypointAsync(player.getUniqueId(), waypointName, null,
                player.getLocation(), new Callback<>() {

            @Override
            public void onSuccess(Waypoint value) {
                MessageUtil.sendMessage(player, createSection, "success");
            }

            @Override
            public void onError(Throwable throwable) {
                CommandWaypoints.this.plugin.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);
                MessageUtil.sendMessage(player, createSection, "error");
            }
        });
    }

    private void renameWaypoint(Player player, ConfigurationSection section, String waypointName, String newWaypointName) {

        ConfigurationSection renameSection = section.getConfigurationSection("rename");

        // Checking that the player has the required permission to use the command.
        if(!player.hasPermission(Permission.COMMAND_WAYPOINTS_RENAME.getName())) {
            MessageUtil.sendMessage(player, section, "errors.no-permission");
            return;
        }

        WaypointUser user = this.waypointUserService.getWaypointUser(player.getUniqueId());

        // Checking player's data.
        if(user == null) {
            MessageUtil.sendMessage(player, section, "errors.no-data");
            return;
        }

        // Checking that the waypoint exists.
        Waypoint waypoint = user.getWaypointByName(waypointName).orElse(null);

        if(waypoint == null) {
            MessageUtil.sendMessage(player, section, "errors.name-not-found");
            return;
        }

        // Checking that the user does not have a waypoint with the same name.
        if(user.hasWaypointByName(newWaypointName)) {
            MessageUtil.sendMessage(player, section, "errors.name-already-exists");
            return;
        }

        // Updating the waypoint.
        waypoint.setName(newWaypointName);

        this.waypointService.updateWaypointAsync(waypoint, new Callback<>() {

            @Override
            public void onSuccess(Waypoint value) {
                MessageUtil.sendMessage(player, renameSection, "success");
            }

            @Override
            public void onError(Throwable throwable) {
                CommandWaypoints.this.plugin.getLogger().log(Level.SEVERE, "An error occurred while renaming the waypoint", throwable);
                MessageUtil.sendMessage(player, renameSection, "error");
            }
        });
    }
}
