package com.github.syr0ws.minewaypoints.platform.spigot.processor;

import com.github.syr0ws.crafter.business.BusinessFailureHandler;
import com.github.syr0ws.crafter.business.BusinessFailureProcessor;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.domain.WaypointUser;
import com.github.syr0ws.minewaypoints.plugin.business.failure.*;
import com.github.syr0ws.minewaypoints.platform.spigot.util.placeholder.CustomPlaceholder;
import com.github.syr0ws.minewaypoints.platform.spigot.util.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class WaypointFailureProcessor extends BusinessFailureProcessor {

    private final Plugin plugin;
    private final Player player;

    private WaypointFailureProcessor(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public static WaypointFailureProcessor of(Plugin plugin, Player player) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(player, "player cannot be null");
        return new WaypointFailureProcessor(plugin, player);
    }

    @BusinessFailureHandler(type = WaypointGenericBusinessFailure.class)
    public void onWaypointGenericBusinessFailure(WaypointGenericBusinessFailure ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.generic");
    }

    @BusinessFailureHandler(type = InvalidWaypointName.class)
    public void onInvalidWaypointName(InvalidWaypointName failure) {
        Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.WAYPOINT_NAME, failure.waypointName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.invalid-name", placeholders);
    }

    @BusinessFailureHandler(type = WaypointWorldChanged.class)
    public void onWaypointWorldChanged(WaypointWorldChanged failure) {
        Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.WAYPOINT_WORLD, failure.worldName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.world-changed", placeholders);
    }

    @BusinessFailureHandler(type = ForbiddenWaypointWorld.class)
    public void onForbiddenWaypointWorld(ForbiddenWaypointWorld failure) {
        Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.WAYPOINT_WORLD, failure.world());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.forbidden-world", placeholders);
    }

    @BusinessFailureHandler(type = WaypointNameAlreadyExists.class)
    public void onWaypointNameAlreadyExists(WaypointNameAlreadyExists failure) {
        Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.WAYPOINT_NAME, failure.waypointName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.name-already-exists", placeholders);
    }

    @BusinessFailureHandler(type = WaypointNameNotFound.class)
    public void onWaypointNameNotFound(WaypointNameNotFound failure) {
        Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.WAYPOINT_NAME, failure.waypointName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.name-not-found", placeholders);
    }

    @BusinessFailureHandler(type = WaypointNotOwned.class)
    public void onWaypointNotOwned(WaypointNotOwned ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.not-found");
    }

    @BusinessFailureHandler(type = TargetUserNotFound.class)
    public void onTargetUserNotFound(TargetUserNotFound ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.player.target-not-found");
    }

    @BusinessFailureHandler(type = WaypointAlreadyShared.class)
    public void onWaypointAlreadyShared(WaypointAlreadyShared failure) {
        WaypointUser target = failure.target();

        if (target.getId().equals(this.player.getUniqueId())) {
            // Case in which the target is the player with which the waypoint is already shared with.
            Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, failure.waypoint());
            MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.already-shared-with-me", placeholders);
        } else {
            // Case in which the target is another player.
            Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, failure.waypoint());
            placeholders.put(CustomPlaceholder.TARGET_NAME, failure.target().getName());
            MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.already-shared-with-target", placeholders);
        }
    }

    @BusinessFailureHandler(type = WaypointNotSharedWithTarget.class)
    public void onWaypointNotSharedWitTarget(WaypointNotSharedWithTarget failure) {
        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, failure.waypoint());
        placeholders.put(CustomPlaceholder.TARGET_NAME, failure.target().getName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.not-shared-with-target", placeholders);
    }

    @BusinessFailureHandler(type = SharingRequestNotFound.class)
    public void onSharingRequestNotFound(SharingRequestNotFound ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.sharing-request.not-found");
    }

    @BusinessFailureHandler(type = SharingRequestToOwner.class)
    public void onSharingRequestToOwner(SharingRequestToOwner failure) {
        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, failure.waypoint());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.sharing-request.to-owner", placeholders);
    }

    @BusinessFailureHandler(type = NoWaypointAccess.class)
    public void onNoWaypointAccess(NoWaypointAccess ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.not-access");
    }

    @BusinessFailureHandler(type = WaypointLimitReached.class)
    public void onWaypointLimitReached(WaypointLimitReached ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.limit-reached");
    }

    @BusinessFailureHandler(type = SameWaypointName.class)
    public void onSameWaypointName(SameWaypointName ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.same-name");
    }
}
