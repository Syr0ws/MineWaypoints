package com.github.syr0ws.minewaypoints.business.failure.processor;

import com.github.syr0ws.crafter.business.BusinessFailureHandler;
import com.github.syr0ws.crafter.business.BusinessFailureProcessor;
import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.business.failure.*;
import com.github.syr0ws.minewaypoints.util.placeholder.CustomPlaceholder;
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

    public static WaypointFailureProcessor of(Plugin plugin, Player player) {
        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(player, "player cannot be null");
        return new WaypointFailureProcessor(plugin, player);
    }
}
