package com.github.syr0ws.minewaypoints.service.platform.impl;

import com.github.syr0ws.crafter.component.EasyTextComponent;
import com.github.syr0ws.crafter.config.ConfigUtil;
import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.cache.WaypointShareCache;
import com.github.syr0ws.minewaypoints.cache.WaypointUserCache;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointOwner;
import com.github.syr0ws.minewaypoints.service.WaypointService;
import com.github.syr0ws.minewaypoints.service.platform.BukkitWaypointService;
import com.github.syr0ws.minewaypoints.util.WaypointValidate;
import com.github.syr0ws.minewaypoints.util.placeholder.CustomPlaceholder;
import com.github.syr0ws.minewaypoints.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class SimpleBukkitWaypointService implements BukkitWaypointService {

    private final Plugin plugin;
    private final WaypointService waypointService;
    private final WaypointUserCache<? extends WaypointOwner> waypointUserCache;
    private final WaypointShareCache waypointShareCache;

    public SimpleBukkitWaypointService(Plugin plugin, WaypointService waypointService, WaypointUserCache<? extends WaypointOwner> waypointUserCache, WaypointShareCache waypointShareCache) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");
        Validate.notNull(waypointUserCache, "waypointUserCache cannot be null");
        Validate.notNull(waypointShareCache, "waypointShareCache cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
        this.waypointUserCache = waypointUserCache;
        this.waypointShareCache = waypointShareCache;
    }

    @Override
    public Optional<Promise<Waypoint>> createWaypoint(Player owner, String waypointName, Location location, Material icon) {

        ConfigurationSection messagesSection = this.getSection("messages");
        ConfigurationSection createSection = this.getSection("messages.waypoint.create");

        WaypointOwner user = this.waypointUserCache.getUser(owner.getUniqueId())
                .orElse(null);

        if (user == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.player.no-data");
            return Optional.empty();
        }

        if (user.hasWaypointByName(waypointName)) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-already-exists", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        if (!WaypointValidate.isValidWaypointName(waypointName)) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-invalid", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        icon = icon == null ? this.getDefaultWaypointIcon() : icon;

        Promise<Waypoint> promise = this.waypointService.createWaypoint(owner.getUniqueId(), waypointName, icon, location)
                .then(waypoint -> {
                    Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                    MessageUtil.sendMessage(owner, createSection, "success", placeholders);
                })
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "An error occurred while creating the waypoint", throwable);
                    MessageUtil.sendMessage(owner, createSection, "error");
                });

        return Optional.of(promise);
    }

    @Override
    public Optional<Promise<Void>> updateWaypointName(Player owner, String waypointName, String newWaypointName) {

        ConfigurationSection messagesSection = this.getSection("messages");
        ConfigurationSection renameSection = this.getSection("messages.waypoint.rename");

        WaypointOwner user = this.waypointUserCache.getUser(owner.getUniqueId())
                .orElse(null);

        if (user == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.player.no-data");
            return Optional.empty();
        }

        Waypoint waypoint = user.getWaypointByName(waypointName).orElse(null);

        if (waypoint == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        if (user.hasWaypointByName(newWaypointName)) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-already-exists", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        if (!WaypointValidate.isValidWaypointName(waypointName)) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-invalid", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
        placeholders.put(CustomPlaceholder.WAYPOINT_OLD_NAME, waypoint.getName());

        Promise<Void> promise = this.waypointService.updateWaypointName(waypoint.getId(), newWaypointName)
                .then(value ->
                        MessageUtil.sendMessage(owner, renameSection, "success", placeholders))
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "An error occurred while renaming the waypoint", throwable);
                    MessageUtil.sendMessage(owner, renameSection, "error", placeholders);
                });

        return Optional.of(promise);
    }

    @Override
    public Optional<Promise<Void>> updateWaypointLocation(Player owner, String waypointName, Location location) {

        ConfigurationSection messagesSection = this.getSection("messages");
        ConfigurationSection relocateSection = this.getSection("messages.waypoint.relocate");

        WaypointOwner user = this.waypointUserCache.getUser(owner.getUniqueId())
                .orElse(null);

        if (user == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.player.no-data");
            return Optional.empty();
        }

        Waypoint waypoint = user.getWaypointByName(waypointName).orElse(null);

        if (waypoint == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        WaypointLocation oldLocation = waypoint.getLocation();

        if(oldLocation.getWorld().equals(location.getWorld().getName())) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.not-same-world"); // TODO
            return Optional.empty();
        }

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
        placeholders.putAll(PlaceholderUtil.getWaypointOldLocationPlaceholders(waypoint.getLocation()));

        Promise<Void> promise = this.waypointService.updateWaypointLocation(waypoint.getId(), location)
                .then(value ->
                        MessageUtil.sendMessage(owner, relocateSection, "success", placeholders))
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "An error occurred while updating the waypoint location", throwable);
                    MessageUtil.sendMessage(owner, relocateSection, "error", placeholders);
                });

        return Optional.of(promise);
    }

    @Override
    public Optional<Promise<Void>> shareWaypoint(Player owner, String waypointName, String targetName) {

        ConfigurationSection messagesSection = this.getSection("messages");
        ConfigurationSection shareSection = this.getSection("messages.waypoint.share");

        WaypointOwner user = this.waypointUserCache.getUser(owner.getUniqueId())
                .orElse(null);

        if (user == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.player.no-data");
            return Optional.empty();
        }

        Waypoint waypoint = user.getWaypointByName(waypointName).orElse(null);

        if (waypoint == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        if (owner.getName().equalsIgnoreCase(targetName)) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.target.equals-sender");
            return Optional.empty();
        }

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.target.not-found", Map.of(CustomPlaceholder.TARGET_NAME, targetName));
            return Optional.empty();
        }

        Promise<Void> promise = new Promise<>((resolve, reject) -> {

            this.waypointService.isWaypointSharedWith(targetName, waypoint.getId())
                    .then(isShared -> {

                        // Checking that the waypoint is not already shared with the target.
                        if(isShared) {
                            Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.TARGET_NAME, target.getName());
                            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.already-shared-with-target", placeholders);
                            return;
                        }

                        UUID requestId = this.waypointShareCache.addSharingRequest(waypoint, target);

                        // Sending a message to the sender.
                        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                        placeholders.put(CustomPlaceholder.TARGET_NAME, targetName);
                        placeholders.put(CustomPlaceholder.SHARE_REQUEST_ID, requestId.toString());

                        EasyTextComponent senderMessage = EasyTextComponent.fromYaml(shareSection.getConfigurationSection("sender"));
                        MessageUtil.sendMessage(owner, senderMessage, placeholders);

                        // Send a sharing proposal to the target.
                        EasyTextComponent targetMessage = EasyTextComponent.fromYaml(shareSection.getConfigurationSection("target"));
                        MessageUtil.sendMessage(target, targetMessage, placeholders);
                    })
                    .except(throwable -> {
                        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                        placeholders.put(CustomPlaceholder.TARGET_NAME, targetName);

                        this.plugin.getLogger().log(Level.SEVERE, "An error occurred while sharing the waypoint", throwable);
                        MessageUtil.sendMessage(owner, shareSection, "error", placeholders);
                    })
                    .resolve();

            resolve.accept(null);
        });

        return Optional.of(promise);
    }

    @Override
    public Optional<Promise<Boolean>> unshareWaypoint(Player owner, String waypointName, String targetName) {

        ConfigurationSection messagesSection = this.getSection("messages");
        ConfigurationSection unshareSection = this.getSection("messages.waypoint.unshare");

        WaypointOwner user = this.waypointUserCache.getUser(owner.getUniqueId())
                .orElse(null);

        // Checking player's data.
        if (user == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.player.no-data");
            return Optional.empty();
        }

        // Checking that the waypoint exists.
        Waypoint waypoint = user.getWaypointByName(waypointName).orElse(null);

        if (waypoint == null) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.waypoint.name-not-found", Map.of(CustomPlaceholder.WAYPOINT_NAME, waypointName));
            return Optional.empty();
        }

        // Checking that the target player is not the sender.
        if (owner.getName().equals(targetName)) {
            MessageUtil.sendMessage(owner, messagesSection, "errors.target.equals-sender");
            return Optional.empty();
        }

        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
        placeholders.put(CustomPlaceholder.TARGET_NAME, targetName);

        Promise<Boolean> promise = this.waypointService.unshareWaypoint(targetName, waypoint.getId())
                .then(unshared -> {

                    if (unshared) {
                        MessageUtil.sendMessage(owner, unshareSection, "not-shared", placeholders);
                    } else {
                        MessageUtil.sendMessage(owner, unshareSection, "success", placeholders);

                        // Sending a message to the target if he is online.
                        Player target = Bukkit.getPlayer(targetName);

                        if (target != null) {
                            MessageUtil.sendMessage(target, unshareSection, "target-unshared", placeholders);
                        }
                    }
                })
                .except(throwable -> {
                    this.plugin.getLogger().log(Level.SEVERE, "An error occurred while unsharing the waypoint", throwable);
                    MessageUtil.sendMessage(owner, unshareSection, "error", placeholders);
                });

        return Optional.of(promise);
    }

    private ConfigurationSection getSection(String path) {

        FileConfiguration config = this.plugin.getConfig();

        ConfigurationSection section = config.getConfigurationSection(path);
        Validate.notNull(section, String.format("Section '%s' not found in the config.yml file", path));

        return section;
    }

    private Material getDefaultWaypointIcon() {
        try {
            return ConfigUtil.getMaterial(this.plugin.getConfig(), "default-waypoint-icon");
        } catch (ConfigurationException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Invalid default waypoint icon");
            return Material.GRASS_BLOCK;
        }
    }
}
