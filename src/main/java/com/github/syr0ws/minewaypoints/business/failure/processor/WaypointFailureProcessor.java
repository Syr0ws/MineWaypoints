package com.github.syr0ws.minewaypoints.business.failure.processor;

import com.github.syr0ws.crafter.business.BusinessFailureHandler;
import com.github.syr0ws.crafter.business.BusinessFailureProcessor;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.failure.*;
import com.github.syr0ws.minewaypoints.util.placeholder.CustomPlaceholder;
import com.github.syr0ws.minewaypoints.util.placeholder.PlaceholderUtil;
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

    @BusinessFailureHandler(type = WaypointGenericBusinessFailure.class)
    public void onWaypointGenericBusinessFailure(WaypointGenericBusinessFailure ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.generic");
    }

    @BusinessFailureHandler(type = InvalidWaypointName.class)
    public void onInvalidWaypointName(InvalidWaypointName failure) {
        Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.WAYPOINT_NAME, failure.waypointName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.invalid-name", placeholders);
    }

    @BusinessFailureHandler(type = InvalidWaypointWorld.class)
    public void onInvalidWaypointWorld(InvalidWaypointWorld failure) {
        Map<Placeholder, String> placeholders = Map.of(CustomPlaceholder.WAYPOINT_WORLD, failure.worldName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.invalid-world", placeholders);
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

    @BusinessFailureHandler(type = WaypointNotFound.class)
    public void onWaypointNotFound(WaypointNotFound ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.not-found");
    }

    @BusinessFailureHandler(type = TargetUserNotFound.class)
    public void onTargetUserNotFound(TargetUserNotFound ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.player.target-not-found");
    }

    @BusinessFailureHandler(type = WaypointAlreadyShared.class)
    public void onWaypointAlreadyShared(WaypointAlreadyShared failure) {
        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, failure.waypoint());
        placeholders.put(CustomPlaceholder.TARGET_NAME, failure.target().getName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.already-shared-with-target", placeholders);
    }

    @BusinessFailureHandler(type = WaypointAlreadyShared.class)
    public void onWaypointAlreadyShared(WaypointNotSharedWithTarget failure) {
        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, failure.waypoint());
        placeholders.put(CustomPlaceholder.TARGET_NAME, failure.target().getName());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.waypoint.not-shared-with-target", placeholders);
    }

    @BusinessFailureHandler(type = SharingRequestNotFound.class)
    public void onSharingRequestNotFound(SharingRequestNotFound ignored) {
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.sharing-request.not-found");
    }

    @BusinessFailureHandler(type = SharingRequestToOwner.class)
    public void onSharingRequestNotFound(SharingRequestToOwner failure) {
        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, failure.waypoint());
        MessageUtil.sendMessage(this.player, this.plugin.getConfig(), "messages.errors.sharing-request.itself", placeholders);
    }

    public static WaypointFailureProcessor of(Plugin plugin, Player player) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(player, "player cannot be null");
        return new WaypointFailureProcessor(plugin, player);
    }
}
