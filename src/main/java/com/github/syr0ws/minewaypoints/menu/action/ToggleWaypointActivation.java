package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.message.MessageUtil;
import com.github.syr0ws.crafter.message.placeholder.Placeholder;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.inventory.InventoryViewer;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.util.DataUtil;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.service.WaypointActivationService;
import com.github.syr0ws.minewaypoints.service.util.WaypointEnums;
import com.github.syr0ws.minewaypoints.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public class ToggleWaypointActivation extends CommonAction {

    public static final String ACTION_NAME = "TOGGLE_WAYPOINT_ACTIVATION";

    private final Plugin plugin;
    private final WaypointActivationService waypointActivationService;

    public ToggleWaypointActivation(Set<ClickType> clickTypes, Plugin plugin, WaypointActivationService waypointActivationService) {
        super(clickTypes);

        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.plugin = plugin;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        FileConfiguration config = this.plugin.getConfig();

        InventoryViewer viewer = event.getViewer();
        Player player = viewer.getPlayer();
        Waypoint waypoint = this.getWaypoint(event);

        // An item is always clicked when toggling a waypoint, so, it cannot be null.
        InventoryItem item = event.getItem().get();
        item.disable();

        this.waypointActivationService.toggleWaypoint(player, waypoint.getId())
                .then(status -> {
                    this.sendToggleMessage(player, waypoint, status);
                    Bukkit.getScheduler().runTask(this.plugin, player::closeInventory);
                })
                .except(throwable -> {
                    MessageUtil.sendMessage(player, config, "messages.waypoint.toggle.error");
                    String message = String.format("An error occurred while toggling waypoint activation for player %s", player.getUniqueId());
                    this.plugin.getLogger().log(Level.SEVERE, message, throwable);
                })
                .complete(item::enable)
                .resolveAsync(this.plugin);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    private Waypoint getWaypoint(CraftVentoryClickEvent event) {

        Optional<Waypoint> waypointOptional = DataUtil.getContextualData(event, CustomDataStoreKey.WAYPOINT, Waypoint.class);

        if(waypointOptional.isPresent()) {
            return waypointOptional.get();
        }

        Optional<WaypointShare> waypointShareOptional = DataUtil.getContextualData(event, CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class);

        if(waypointShareOptional.isPresent()) {
            return waypointShareOptional.get().getWaypoint();
        }

        throw new IllegalStateException("No waypoint found");
    }

    private void sendToggleMessage(Player player, Waypoint waypoint, WaypointEnums.WaypointToggleStatus status) {

        FileConfiguration config = this.plugin.getConfig();
        Map<Placeholder, String> placeholders = PlaceholderUtil.getWaypointPlaceholders(this.plugin, waypoint);

        String key = switch (status) {
            case ACTIVATED -> "messages.waypoint.toggle.activated";
            case DEACTIVATED -> "messages.waypoint.toggle.deactivated";
            case NO_WAYPOINT_ACCESS -> "messages.waypoint.errors.no-access";
            case WAYPOINT_NOT_FOUND -> "messages.waypoint.errors.not-found";
        };

        MessageUtil.sendMessage(player, config, key, placeholders);
    }
}
