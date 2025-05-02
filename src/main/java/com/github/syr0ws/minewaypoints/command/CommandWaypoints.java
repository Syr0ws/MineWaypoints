package com.github.syr0ws.minewaypoints.command;

import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.InventoryService;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.minewaypoints.plugin.cache.WaypointOwnerCache;
import com.github.syr0ws.minewaypoints.menu.WaypointsMenuDescriptor;
import com.github.syr0ws.minewaypoints.plugin.domain.Waypoint;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointOwner;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import com.github.syr0ws.minewaypoints.util.Permission;
import com.github.syr0ws.minewaypoints.util.placeholder.CustomPlaceholder;
import com.github.syr0ws.smartcommands.api.*;
import com.github.syr0ws.smartcommands.api.argument.CommandArgumentTree;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class CommandWaypoints extends SmartCommand {

    private final Plugin plugin;
    private final InventoryService inventoryService;
    private final BukkitWaypointService waypointService;
    private final WaypointOwnerCache<? extends WaypointOwner> waypointOwnerCache;

    public CommandWaypoints(Plugin plugin, InventoryService inventoryService, BukkitWaypointService waypointService, WaypointOwnerCache<? extends WaypointOwner> waypointOwnerCache) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(inventoryService, "inventoryService cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");
        Validate.notNull(waypointOwnerCache, "waypointOwnerCache cannot be null");

        this.plugin = plugin;
        this.inventoryService = inventoryService;
        this.waypointService = waypointService;
        this.waypointOwnerCache = waypointOwnerCache;
    }

    @Override
    public void configure() {
        CommandArgumentTree tree = super.getTree();
        tree.addArgumentValueProvider("rename.[waypoint_name]", sender -> this.getWaypointNames((Player) sender));
        tree.addArgumentValueProvider("relocate.[waypoint_name]", sender -> this.getWaypointNames((Player) sender));
        tree.addArgumentValueProvider("share.[waypoint_name]", sender -> this.getWaypointNames((Player) sender));
        tree.addArgumentValueProvider("share.[waypoint_name].[target_name]", sender -> this.getOnlineTargets((Player) sender));
    }

    @Override
    public void onNotAllowedSenderType(CommandSender sender, Command command) {
        sender.sendMessage("You must be a player to execute this command.");
    }

    @Override
    public void onNoPermission(CommandSender sender, Command command) {
        MessageUtil.sendMessage((Player) sender, this.plugin.getConfig(), "messages.errors.command.no-permission");
    }

    @Override
    public String getName() {
        return "waypoints";
    }

    @Command(args = {"help"}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS)
    public void sendHelp(CommandExecutionContext context) {
        CommandSender sender = context.sender();
        String[] usages = super.getAllUsages(sender);
        sender.sendMessage(usages);
    }

    @Command(args = {}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS)
    @CommandUsage(message = "{usages.show}")
    public void showWaypoints(CommandExecutionContext context) {

        ConfigurationSection section = this.getCommandSection();

        Player player = (Player) context.sender();
        MessageUtil.sendMessage(player, section, "show-waypoints");

        InventoryViewer viewer = this.inventoryService.getInventoryViewer(player);

        this.inventoryService.getProvider(WaypointsMenuDescriptor.MENU_ID).ifPresent(provider -> {
            CraftVentory inventory = provider.createInventory(this.inventoryService, player);
            viewer.getViewManager().openView(inventory, true);
        });
    }

    @Command(args = {"create", "[waypoint_name]"}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS_CREATE)
    @CommandUsage(message = "{usages.create}")
    public void createWaypoint(CommandExecutionContext context) {

        Player player = (Player) context.sender();
        String waypointName = context.getArgumentValue("[waypoint_name]");

        // Creating the waypoint.
        this.waypointService.createWaypoint(player, waypointName, null, player.getLocation())
                .resolveAsync(this.plugin);
    }

    @Command(args = {"rename", "[waypoint_name]", "[waypoint_new_name]"}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS_RENAME)
    @CommandUsage(message = "{usages.rename}")
    public void renameWaypoint(CommandExecutionContext context) {

        Player player = (Player) context.sender();
        String waypointName = context.getArgumentValue("[waypoint_name]");
        String newWaypointName = context.getArgumentValue("[waypoint_new_name]");

        // Renaming the waypoint.
        this.waypointService.updateWaypointNameByName(player, waypointName, newWaypointName)
                .resolveAsync(this.plugin);
    }

    @Command(args = {"relocate", "[waypoint_name]"}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS_RELOCATE)
    @CommandUsage(message = "{usages.relocate}")
    public void changeWaypointLocation(CommandExecutionContext context) {

        Player player = (Player) context.sender();
        String waypointName = context.getArgumentValue("[waypoint_name]");

        // Updating the location of the waypoint.
        this.waypointService.updateWaypointLocationByName(player, waypointName, player.getLocation())
                .resolveAsync(this.plugin);
    }

    @Command(args = {"share", "[waypoint_name]", "[target_name]"}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS_SHARE)
    @CommandUsage(message = "{usages.share}")
    public void sendWaypointSharingRequest(CommandExecutionContext context) {

        Player player = (Player) context.sender();
        String waypointName = context.getArgumentValue("[waypoint_name]");
        String targetName = context.getArgumentValue("[target_name]");

        FileConfiguration config = this.plugin.getConfig();

        // Checking that the target is online to be able to accept the share proposal.
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            MessageUtil.sendMessage(player, config, "messages.errors.player.target-not-found", Map.of(CustomPlaceholder.TARGET_NAME, targetName));
            return;
        }

        this.waypointService.sendWaypointSharingRequest(player, waypointName, target)
                .resolveAsync(this.plugin);
    }

    @Command(args = {"sharing-request", "accept", "[request_id]"}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS_SHARE, autoComplete = false)
    public void acceptWaypointSharingRequest(CommandExecutionContext context) {

        Player player = (Player) context.sender();
        String requestId = context.getArgumentValue("[request_id]");

        FileConfiguration config = this.plugin.getConfig();

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

    @Command(args = {"sharing-request", "cancel", "[request_id]"}, allowedSenders = {CommandSenderType.PLAYER}, permission = Permission.COMMAND_WAYPOINTS_SHARE, autoComplete = false)
    public void cancelWaypointSharingRequest(CommandExecutionContext context) {

        Player player = (Player) context.sender();
        String requestId = context.getArgumentValue("[request_id]");

        FileConfiguration config = this.plugin.getConfig();

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

    @Override
    public ConfigurationSection getCommandSection() {

        FileConfiguration config = this.plugin.getConfig();

        ConfigurationSection section = config.getConfigurationSection("messages.command.waypoint");
        Validate.notNull(section, "Section 'messages.command.waypoint' not found");

        return section;
    }

    private List<String> getOnlineTargets(Player requester) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.equals(requester))
                .map(HumanEntity::getName)
                .collect(Collectors.toList());
    }

    private List<String> getWaypointNames(Player player) {
        return this.waypointOwnerCache.getOwner(player.getUniqueId())
                .map(owner ->
                        owner.getWaypoints().stream().map(Waypoint::getName).collect(Collectors.toList())
                ).orElse(new ArrayList<>());
    }
}
