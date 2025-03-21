package com.github.syr0ws.minewaypoints.command;

import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import com.github.syr0ws.minewaypoints.util.Permission;
import com.github.syr0ws.minewaypoints.util.placeholder.CustomPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandWaypoints implements CommandExecutor {

    private final Plugin plugin;
    private final InventoryService inventoryService;
    private final BukkitWaypointService waypointService;

    public CommandWaypoints(Plugin plugin, InventoryService inventoryService, BukkitWaypointService waypointService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(inventoryService, "inventoryService cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.inventoryService = inventoryService;
        this.waypointService = waypointService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration config = this.plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("messages.command.waypoint");
        Validate.notNull(section, "Section 'command-waypoints' cannot be null");

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }

        // Checking that the player has the permission to execute the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS.getName())) {
            MessageUtil.sendMessage(player, config, "messages.errors.command.no-permission");
            return true;
        }

        // Command /waypoints
        if (args.length == 0) {
            this.showWaypoints(player, section);
            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("help")) {
                this.sendHelp(player, section);
                return true;
            }
        }

        if (args.length == 2) {

            // Command /waypoints create <name>
            if (args[0].equalsIgnoreCase("create")) {
                this.createWaypoint(player, args[1]);
                return true;
            }

            // Command /waypoints relocate <name>
            if (args[0].equalsIgnoreCase("relocate")) {
                this.changeWaypointLocation(player, args[1]);
                return true;
            }
        }

        if (args.length == 3) {

            // Command /waypoints rename <old_name> <new_name>
            if (args[0].equalsIgnoreCase("rename")) {
                this.renameWaypoint(player, args[1], args[2]);
                return true;
            }

            // Command /waypoints share <waypoint_name> <target>
            if (args[0].equalsIgnoreCase("share")) {
                this.sendWaypointSharingRequest(player, args[1], args[2]);
                return true;
            }

            // Command /waypoints sharing-request <action> <request_id>
            if (args[0].equalsIgnoreCase("sharing-request")) {

                // Command /waypoints sharing-request accept <request_id>
                if (args[1].equalsIgnoreCase("accept")) {
                    this.acceptWaypointSharingRequest(player, args[2]);
                    return true;
                }

                // Command /waypoints sharing-request cancel <request_id>
                if (args[1].equalsIgnoreCase("cancel")) {
                    this.cancelWaypointSharingRequest(player, args[2]);
                    return true;
                }
            }
        }

        this.sendHelp(player, section);

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

    private void sendHelp(Player player, ConfigurationSection section) {
        List<String> usages = section.getStringList("help");
        MessageUtil.sendMessages(player, usages);
    }

    private void createWaypoint(Player player, String waypointName) {

        // Checking that the player has the required permission to use the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS_CREATE.getName())) {
            MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.errors.command.no-permission");
            return;
        }

        // Creating the waypoint.
        this.waypointService.createWaypoint(player, waypointName, null, player.getLocation())
                .resolveAsync(this.plugin);
    }

    private void renameWaypoint(Player player, String waypointName, String newWaypointName) {

        // Checking that the player has the required permission to use the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS_RENAME.getName())) {
            MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.errors.command.no-permission");
            return;
        }

        // Renaming the waypoint.
        this.waypointService.updateWaypointNameByName(player, waypointName, newWaypointName)
                .resolveAsync(this.plugin);
    }

    private void changeWaypointLocation(Player player, String waypointName) {

        // Checking that the player has the required permission to use the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS_RELOCATE.getName())) {
            MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.errors.command.no-permission");
            return;
        }

        // Updating the location of the waypoint.
        this.waypointService.updateWaypointLocationByName(player, waypointName, player.getLocation())
                .resolveAsync(this.plugin);
    }

    public void sendWaypointSharingRequest(Player player, String waypointName, String targetName) {

        FileConfiguration config = this.plugin.getConfig();

        // Checking that the player has the required permission to use the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS_SHARE.getName())) {
            MessageUtil.sendMessage(player, config, "messages.errors.command.no-permission");
            return;
        }

        // Checking that the target is online to be able to accept the share proposal.
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            MessageUtil.sendMessage(player, config, "messages.errors.player.target-not-found", Map.of(CustomPlaceholder.TARGET_NAME, targetName));
            return;
        }

        this.waypointService.sendWaypointSharingRequest(player, waypointName, target)
                .resolveAsync(this.plugin);
    }

    private void acceptWaypointSharingRequest(Player player, String requestId) {

        FileConfiguration config = this.plugin.getConfig();

        // Checking that the player has the required permission to use the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS_SHARE.getName())) {
            MessageUtil.sendMessage(player, config, "messages.errors.command.no-permission");
            return;
        }

        // Checking that the sharing request id is valid.
        if (!Validate.isUUID(requestId)) {
            MessageUtil.sendMessage(player, config, "messages.errors.sharing-request.invalid-request-id");
            return;
        }

        UUID requestUUID = UUID.fromString(requestId);

        // Sharing the waypoint.
        this.waypointService.acceptWaypointSharingRequest(player, requestUUID)
                .resolveAsync(this.plugin);
    }

    private void cancelWaypointSharingRequest(Player player, String requestId) {

        FileConfiguration config = this.plugin.getConfig();

        // Checking that the player has the required permission to use the command.
        if (!player.hasPermission(Permission.COMMAND_WAYPOINTS_SHARE.getName())) {
            MessageUtil.sendMessage(player, config, "messages.errors.command.no-permission");
            return;
        }

        // Checking that the sharing request id is valid.
        if (!Validate.isUUID(requestId)) {
            MessageUtil.sendMessage(player, config, "messages.errors.sharing-request.invalid-request-id");
            return;
        }

        UUID requestUUID = UUID.fromString(requestId);

        // Sharing the waypoint.
        this.waypointService.cancelWaypointSharingRequest(player, requestUUID)
                .resolveAsync(this.plugin);
    }
}
