package com.github.syr0ws.minewaypoints.platform.impl;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.component.EasyTextComponent;
import com.github.syr0ws.crafter.config.ConfigUtil;
import com.github.syr0ws.crafter.config.ConfigurationException;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.service.BusinessWaypointService;
import com.github.syr0ws.minewaypoints.event.WaypointDeleteEvent;
import com.github.syr0ws.minewaypoints.event.WaypointUnshareEvent;
import com.github.syr0ws.minewaypoints.event.WaypointUpdateEvent;
import com.github.syr0ws.minewaypoints.model.*;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointService;
import com.github.syr0ws.minewaypoints.platform.processor.WaypointFailureProcessor;
import com.github.syr0ws.minewaypoints.util.placeholder.CustomPlaceholder;
import com.github.syr0ws.minewaypoints.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

public class SimpleBukkitWaypointService implements BukkitWaypointService {

    private final Plugin plugin;
    private final BusinessWaypointService waypointService;

    public SimpleBukkitWaypointService(Plugin plugin, BusinessWaypointService waypointService) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointService, "waypointService cannot be null");

        this.plugin = plugin;
        this.waypointService = waypointService;
    }

    @Override
    public Promise<BusinessResult<Waypoint, BusinessFailure>> createWaypoint(Player owner, String name, Material icon, Location location) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(name, "name cannot be null or empty");
        Validate.notNull(location, "location cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<BusinessResult<Waypoint, BusinessFailure>>((resolve, reject) -> {

            Material waypointIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            BusinessResult<Waypoint, BusinessFailure> result = this.waypointService.createWaypoint(ownerId, name, waypointIcon.name(), location);
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(waypoint -> {

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.waypoint.create.success", placeholders);

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while creating the waypoint", throwable);
            MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<Waypoint, BusinessFailure>> updateWaypointNameByName(Player owner, String waypointName, String newName) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notEmpty(newName, "newName cannot be null or empty");

        UUID ownerId = owner.getUniqueId();

        return new Promise<BusinessResult<Waypoint, BusinessFailure>>((resolve, reject) -> {

            BusinessResult<Waypoint, BusinessFailure> result = this.waypointService.updateWaypointName(ownerId, waypointName, newName);
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(waypoint -> {

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                placeholders.put(CustomPlaceholder.WAYPOINT_OLD_NAME, waypointName);
                MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.waypoint.rename.success", placeholders);

                // Event must be called synchronously.
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    WaypointUpdateEvent event = new WaypointUpdateEvent(waypoint);
                    Bukkit.getPluginManager().callEvent(event);
                });

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while renaming the waypoint", throwable);
            MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<Waypoint, BusinessFailure>> updateWaypointLocationByName(Player owner, String waypointName, Location location) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notNull(location, "location cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<BusinessResult<Waypoint, BusinessFailure>>((resolve, reject) -> {

            WaypointLocation waypointLocation = WaypointLocation.fromLocation(location);

            BusinessResult<Waypoint, BusinessFailure> result = this.waypointService.updateWaypointLocation(ownerId, waypointName, waypointLocation);
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(waypoint -> {

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.waypoint.update-location.success", placeholders);

                // Event must be called synchronously.
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    WaypointUpdateEvent event = new WaypointUpdateEvent(waypoint);
                    Bukkit.getPluginManager().callEvent(event);
                });

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while updating the location of the waypoint", throwable);
            MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<Waypoint, BusinessFailure>> updateWaypointIconById(Player owner, long waypointId, Material icon) {
        Validate.notNull(owner, "owner cannot be null");

        UUID ownerId = owner.getUniqueId();

        return new Promise<BusinessResult<Waypoint, BusinessFailure>>((resolve, reject) -> {

            Material waypointIcon = icon == null ? this.getDefaultWaypointIcon() : icon;

            BusinessResult<Waypoint, BusinessFailure> result = this.waypointService.updateWaypointIcon(ownerId, waypointId, waypointIcon.name());
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(waypoint -> {

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.waypoint.icon-update.success", placeholders);

                // Event must be called synchronously.
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    WaypointUpdateEvent event = new WaypointUpdateEvent(waypoint);
                    Bukkit.getPluginManager().callEvent(event);
                });

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while updating the icon of the waypoint", throwable);
            MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<Waypoint, BusinessFailure>> deleteWaypoint(Player owner, long waypointId) {
        Validate.notNull(owner, "owner cannot be null");

        UUID ownerId = owner.getUniqueId();

        Set<WaypointUser> sharedWith = new HashSet<>();

        return new Promise<BusinessResult<Waypoint, BusinessFailure>>((resolve, reject) -> {

            sharedWith.addAll(this.waypointService.getSharedWith(waypointId).stream()
                    .map(WaypointShare::getSharedWith)
                    .toList());

            BusinessResult<Waypoint, BusinessFailure> result = this.waypointService.deleteWaypoint(ownerId, waypointId)
                    .onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(waypoint -> {

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.waypoint.delete.success", placeholders);

                // Sending a message to online players the waypoint is shared with.
                sharedWith.stream().map(WaypointUser::getPlayer).filter(Objects::nonNull).forEach(player -> {
                    MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.waypoint.delete.to-shared-with", placeholders);
                });

                // Event must be called synchronously.
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    WaypointDeleteEvent event = new WaypointDeleteEvent(waypoint);
                    Bukkit.getPluginManager().callEvent(event);
                });

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while deleting the waypoint", throwable);
            MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<WaypointSharingRequest, BusinessFailure>> sendWaypointSharingRequest(Player owner, String waypointName, Player target) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notEmpty(waypointName, "waypointName cannot be null or empty");
        Validate.notNull(target, "target cannot be null");

        FileConfiguration config = this.plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("messages.waypoint.sharing-request");

        return new Promise<BusinessResult<WaypointSharingRequest, BusinessFailure>>((resolve, reject) -> {

            BusinessResult<WaypointSharingRequest, BusinessFailure> result = this.waypointService.createWaypointSharingRequest(
                    owner.getUniqueId(), waypointName, target.getUniqueId()
            );
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(request -> {

                // Sending a message to the owner.
                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, request.waypoint());
                placeholders.put(CustomPlaceholder.TARGET_NAME, target.getName());
                placeholders.put(CustomPlaceholder.SHARE_REQUEST_ID, request.requestId().toString());

                EasyTextComponent senderMessage = EasyTextComponent.fromYaml(section.getConfigurationSection("owner"));
                MessageUtil.sendMessage(owner, senderMessage, placeholders);

                // Send a sharing proposal to the target.
                EasyTextComponent targetMessage = EasyTextComponent.fromYaml(section.getConfigurationSection("target"));
                MessageUtil.sendMessage(target, targetMessage, placeholders);

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while creating the waypoint sharing request", throwable);
            MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<WaypointShare, BusinessFailure>> acceptWaypointSharingRequest(Player player, UUID requestId) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(requestId, "requestId cannot be null");

        FileConfiguration config = this.plugin.getConfig();

        return new Promise<BusinessResult<WaypointShare, BusinessFailure>>((resolve, reject) -> {

            BusinessResult<WaypointShare, BusinessFailure> result = this.waypointService.acceptWaypointSharingRequest(requestId);
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(share -> {

                Waypoint waypoint = share.getWaypoint();
                WaypointUser owner = waypoint.getOwner();
                WaypointUser target = share.getSharedWith();

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);

                MessageUtil.sendMessage(player, config, "messages.waypoint.sharing-request.accept.target", placeholders);

                Player ownerPlayer = Bukkit.getPlayer(owner.getId());

                if (ownerPlayer != null) {
                    placeholders.put(CustomPlaceholder.TARGET_NAME, target.getName());
                    MessageUtil.sendMessage(ownerPlayer, config, "messages.waypoint.sharing-request.accept.owner", placeholders);
                }

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, player).process(failure));
        });
    }

    @Override
    public Promise<BusinessResult<WaypointSharingRequest, BusinessFailure>> cancelWaypointSharingRequest(Player player, UUID requestId) {
        Validate.notNull(player, "player cannot be null");
        Validate.notNull(requestId, "requestId cannot be null");

        FileConfiguration config = this.plugin.getConfig();

        ConfigurationSection section = config.getConfigurationSection("messages.waypoint.sharing-request");
        Validate.notNull(section, "section 'messages.waypoint.sharing-request' cannot be null");

        return new Promise<BusinessResult<WaypointSharingRequest, BusinessFailure>>((resolve, reject) -> {

            BusinessResult<WaypointSharingRequest, BusinessFailure> result = this.waypointService.cancelWaypointSharingRequest(requestId);
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(request -> {

                Waypoint waypoint = request.waypoint();

                Player owner = Bukkit.getPlayer(waypoint.getOwner().getId());
                Player target = Bukkit.getPlayer(request.target().getId());

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);
                placeholders.put(CustomPlaceholder.TARGET_NAME, request.target().getName());

                if (player.getUniqueId().equals(request.target().getId())) {
                    // Case in which the player who cancelled the request is the target.
                    MessageUtil.sendMessage(player, section, "cancel.by-target-to-target", placeholders);

                    if (owner != null) {
                        MessageUtil.sendMessage(owner, section, "cancel.by-target-to-owner", placeholders);
                    }
                } else {
                    // Case in which the player who cancelled the request is the waypoint owner.
                    MessageUtil.sendMessage(player, section, "cancel.by-owner-to-owner", placeholders);

                    if (target != null) {
                        MessageUtil.sendMessage(target, section, "cancel.by-owner-to-target", placeholders);
                    }
                }

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, player).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while cancelling the waypoint sharing request", throwable);
            MessageUtil.sendMessage(player, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<WaypointShare, BusinessFailure>> unshareWaypointByOwner(Player owner, long waypointId, UUID targetId) {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notNull(targetId, "targetId cannot be null");

        FileConfiguration config = this.plugin.getConfig();

        return new Promise<BusinessResult<WaypointShare, BusinessFailure>>((resolve, reject) -> {

            BusinessResult<WaypointShare, BusinessFailure> result = this.waypointService.unshareWaypointByOwner(owner.getUniqueId(), waypointId, targetId);
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(share -> {

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointSharePlaceholders(this.plugin, share);
                MessageUtil.sendMessage(owner, config, "messages.waypoint.unshare.by-owner-to-owner", placeholders);

                Player target = Bukkit.getPlayer(targetId);

                if (target != null) {
                    MessageUtil.sendMessage(target, config, "messages.waypoint.unshare.by-owner-to-target", placeholders);
                }

                // Event must be called synchronously.
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    WaypointUnshareEvent event = new WaypointUnshareEvent(share.getWaypoint(), share.getSharedWith());
                    Bukkit.getPluginManager().callEvent(event);
                });

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, owner).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while unsharing the waypoint by its owner", throwable);
            MessageUtil.sendMessage(owner, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<BusinessResult<WaypointShare, BusinessFailure>> unshareWaypointBySharedWith(Player sharedWith, long waypointId) {
        Validate.notNull(sharedWith, "sharedWith cannot be null");

        FileConfiguration config = this.plugin.getConfig();

        return new Promise<BusinessResult<WaypointShare, BusinessFailure>>((resolve, reject) -> {

            BusinessResult<WaypointShare, BusinessFailure> result = this.waypointService.unshareWaypointBySharedWith(waypointId, sharedWith.getUniqueId());
            resolve.accept(result);

        }).then(result -> {

            result.onSuccess(share -> {

                Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointSharePlaceholders(this.plugin, share);
                MessageUtil.sendMessage(sharedWith, config, "messages.waypoint.unshare.by-shared-with-to-shared-with", placeholders);

                // Event must be called synchronously.
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    WaypointUnshareEvent event = new WaypointUnshareEvent(share.getWaypoint(), share.getSharedWith());
                    Bukkit.getPluginManager().callEvent(event);
                });

            }).onFailure(failure -> WaypointFailureProcessor.of(this.plugin, sharedWith).process(failure));

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while unsharing the waypoint by its owner", throwable);
            MessageUtil.sendMessage(sharedWith, this.plugin.getConfig(), "messages.errors.generic");
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWaypoints(Player player) {
        Validate.notNull(player, "player cannot be null");

        return new Promise<List<WaypointShare>>((resolve, reject) -> {

            List<WaypointShare> shares = this.waypointService.getSharedWaypoints(player.getUniqueId());
            resolve.accept(shares);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while retrieving shared waypoints", throwable);
        });
    }

    @Override
    public Promise<List<WaypointShare>> getSharedWith(Player owner, long waypointId) {
        Validate.notNull(owner, "owner cannot be null");

        return new Promise<List<WaypointShare>>((resolve, reject) -> {

            List<WaypointShare> shares = this.waypointService.getSharedWith(waypointId);
            resolve.accept(shares);

        }).except(throwable -> {
            this.plugin.getLogger().log(Level.SEVERE, "An error occurred while retrieving shared waypoints", throwable);
        });
    }

    @Override
    public Material getDefaultWaypointIcon() {
        try {
            return ConfigUtil.getMaterial(this.plugin.getConfig(), "default-waypoint-icon");
        } catch (ConfigurationException exception) {
            throw new IllegalArgumentException("Cannot assign waypoint icon", exception);
        }
    }
}
