package com.github.syr0ws.minewaypoints.command;

import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.util.CustomPlaceholder;
import com.github.syr0ws.minewaypoints.util.Permission;
import com.github.syr0ws.minewaypoints.util.PlaceholderUtil;
import com.github.syr0ws.plugincrafter.message.MessageUtil;
import com.github.syr0ws.plugincrafter.message.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CommandWaypoints implements CommandExecutor {

    private final Plugin plugin;
    private final InventoryService inventoryService;
    private final WaypointService waypointService;
    private final WaypointUserCache<? extends WaypointOwner> waypointUserCache;

    public CommandWaypoints(Plugin plugin,
                            InventoryService inventoryService,
                            WaypointService waypointService,
                            WaypointUserCache<? extends WaypointOwner> waypointUserCache) {

        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }

        if (inventoryService == null) {
            throw new IllegalArgumentException("inventoryService cannot be null");
        }

        if (waypointService == null) {
            throw new IllegalArgumentException("waypointService cannot be null");
        }

        if(waypointUserCache == null) {
            throw new IllegalArgumentException("waypointUserCache cannot be null");
        }

        this.plugin = plugin;
        this.inventoryService = inventoryService;
        this.waypointService = waypointService;
        this.waypointUserCache = waypointUserCache;
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

            // Command /waypoints relocate <name>
            if(args[0].equalsIgnoreCase("relocate")) {
                this.changeWaypointLocation(player, section, args[1]);
                return true;
            }
        }

        if(args.length == 3) {

            // Command /waypoints rename <old_name> <new_name>
            if(args[0].equalsIgnoreCase("rename")) {
                this.renameWaypoint(player, section, args[1], args[2]);
                return true;
            }

            // Command /waypoints share <target> <waypoint_name>
            if(args[0].equalsIgnoreCase("share")) {
                this.shareWaypoint(player, section, args[1], args[2]);
                return true;
            }

            // Command /waypoints unshare <target> <waypoint_name>
            if(args[0].equalsIgnoreCase("unshare")) {
                this.unshareWaypoint(player, section, args[1], args[2]);
                return true;
            }
        }

        List<String> usages = section.getStringList("usages");
        MessageUtil.sendMessages(player, usages);

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

        WaypointOwner user = this.waypointUserCache.getUser(player.getUniqueId())
                .orElse(null);

        // Checking player's data.
        if(user == null) {
            MessageUtil.sendMessage(player, section, "errors.no-player-data");
            return;
        }

        // Checking that the user does not have a waypoint with the same name.
        if(user.hasWaypointByName(waypointName)) {
            MessageUtil.sendMessage(player, section, "errors.waypoint.name-already-exists", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return;
        }

        Location location = player.getLocation();

        // Creating the waypoint.
        this.waypointService.createWaypoint(player.getUniqueId(), waypointName, null, location)
                .then(waypoint -> {
                    Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(waypoint);
                    MessageUtil.sendMessage(player, createSection, "success", placeholders);
                })
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);
                    MessageUtil.sendMessage(player, createSection, "error");
                })
                .resolveAsync(this.plugin);
    }

    private void renameWaypoint(Player player, ConfigurationSection section, String waypointName, String newWaypointName) {

        ConfigurationSection renameSection = section.getConfigurationSection("rename");

        // Checking that the player has the required permission to use the command.
        if(!player.hasPermission(Permission.COMMAND_WAYPOINTS_RENAME.getName())) {
            MessageUtil.sendMessage(player, section, "errors.no-permission");
            return;
        }

        WaypointOwner user = this.waypointUserCache.getUser(player.getUniqueId())
                .orElse(null);

        // Checking player's data.
        if(user == null) {
            MessageUtil.sendMessage(player, section, "errors.no-player-data");
            return;
        }

        // Checking that the waypoint exists.
        Waypoint waypoint = user.getWaypointByName(waypointName).orElse(null);

        if(waypoint == null) {
            MessageUtil.sendMessage(player, section, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return;
        }

        // Checking that the user does not have a waypoint with the same name.
        if(user.hasWaypointByName(newWaypointName)) {
            MessageUtil.sendMessage(player, section, "errors.waypoint.name-already-exists", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return;
        }

        String oldName = waypoint.getName();

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(waypoint);
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_NAME, oldName);

        // Updating the waypoint.
        this.waypointService.updateWaypointName(waypoint.getId(), newWaypointName)
                .then(value ->
                        MessageUtil.sendMessage(player, renameSection, "success", placeholders))
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "An error occurred while renaming the waypoint", throwable);
                    MessageUtil.sendMessage(player, renameSection, "error", placeholders);
                })
                .resolveAsync(this.plugin);
    }

    private void changeWaypointLocation(Player player, ConfigurationSection section, String waypointName) {

        ConfigurationSection relocateSection = section.getConfigurationSection("relocate");

        // Checking that the player has the required permission to use the command.
        if(!player.hasPermission(Permission.COMMAND_WAYPOINTS_RELOCATE.getName())) {
            MessageUtil.sendMessage(player, section, "errors.no-permission");
            return;
        }

        WaypointOwner user = this.waypointUserCache.getUser(player.getUniqueId())
                .orElse(null);

        // Checking player's data.
        if(user == null) {
            MessageUtil.sendMessage(player, section, "errors.no-player-data");
            return;
        }

        // Checking that the waypoint exists.
        Waypoint waypoint = user.getWaypointByName(waypointName).orElse(null);

        if(waypoint == null) {
            MessageUtil.sendMessage(player, section, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return;
        }

        WaypointLocation oldLocation = waypoint.getLocation();

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(waypoint);
        placeholders.putAll(PlaceholderUtil.getWaypointOldLocationPlaceholders(oldLocation));

        // Updating the waypoint.
        this.waypointService.updateWaypointLocation(waypoint.getId(), player.getLocation())
                .then(value ->
                        MessageUtil.sendMessage(player, relocateSection, "success", placeholders))
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "An error occurred while relocating the waypoint", throwable);
                    MessageUtil.sendMessage(player, relocateSection, "error", placeholders);
                })
                .resolveAsync(this.plugin);
    }

    public void shareWaypoint(Player player, ConfigurationSection section, String targetName, String waypointName) {

        ConfigurationSection shareSection = section.getConfigurationSection("share");

        // Checking that the player has the required permission to use the command.
        if(!player.hasPermission(Permission.COMMAND_WAYPOINTS_SHARE.getName())) {
            MessageUtil.sendMessage(player, section, "errors.no-permission");
            return;
        }

        WaypointOwner owner = this.waypointUserCache.getUser(player.getUniqueId())
                .orElse(null);

        // Checking player's data.
        if(owner == null) {
            MessageUtil.sendMessage(player, section, "errors.no-player-data");
            return;
        }

        // Checking that the waypoint exists.
        Waypoint waypoint = owner.getWaypointByName(waypointName).orElse(null);

        if(waypoint == null) {
            MessageUtil.sendMessage(player, section, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return;
        }

        // Checking that the target player is not the sender.
        if(player.getName().equals(targetName)) {
            MessageUtil.sendMessage(player, section, "errors.target.equals-sender");
            return;
        }

        // Checking that the target is online to be able to accept the share proposal.
        Player target = Bukkit.getPlayer(targetName);

        if(target == null) {
            MessageUtil.sendMessage(player, section, "errors.target.not-found", Map.of(CustomPlaceholder.TARGET_NAME, targetName));
            return;
        }

        // Send a sharing proposal to the target.
    }

    private void unshareWaypoint(Player player, ConfigurationSection section, String targetName, String waypointName) {

        ConfigurationSection unshareSection = section.getConfigurationSection("unshare");

        // Checking that the player has the required permission to use the command.
        if(!player.hasPermission(Permission.COMMAND_WAYPOINTS_UNSHARE.getName())) {
            MessageUtil.sendMessage(player, section, "errors.no-permission");
            return;
        }

        WaypointOwner owner = this.waypointUserCache.getUser(player.getUniqueId())
                .orElse(null);

        // Checking player's data.
        if(owner == null) {
            MessageUtil.sendMessage(player, section, "errors.no-player-data");
            return;
        }

        // Checking that the waypoint exists.
        Waypoint waypoint = owner.getWaypointByName(waypointName).orElse(null);

        if(waypoint == null) {
            MessageUtil.sendMessage(player, section, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return;
        }

        // Checking that the target player is not the sender.
        if(player.getName().equals(targetName)) {
            MessageUtil.sendMessage(player, section, "errors.target.equals-sender");
            return;
        }

        Player target = Bukkit.getPlayer(targetName);

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(waypoint);
        placeholders.put(CustomPlaceholder.TARGET_NAME, targetName);

        this.waypointService.unshareWaypoint(targetName, waypoint.getId())
                .then(unshared -> {

                    if(unshared) {
                        MessageUtil.sendMessage(player, unshareSection, "not-shared", placeholders);
                    } else {
                        MessageUtil.sendMessage(player, unshareSection, "success", placeholders);

                        // Sending a message to the target if he is online.
                        if(target != null) {
                            MessageUtil.sendMessage(target, unshareSection, "target-unshared", placeholders);
                        }
                    }
                })
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, throwable.getMessage(), throwable);
                    MessageUtil.sendMessage(player, unshareSection, "error", placeholders);
                })
                .resolveAsync(this.plugin);
    }
}
