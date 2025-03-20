package com.github.syr0ws.minewaypoints.menu.action;

import com.github.syr0ws.crafter.business.BusinessFailure;
import com.github.syr0ws.crafter.business.BusinessResult;
import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.craftventory.api.inventory.CraftVentory;
import com.github.syr0ws.craftventory.api.inventory.action.ClickType;
import com.github.syr0ws.craftventory.api.inventory.data.DataStore;
import com.github.syr0ws.craftventory.api.inventory.event.CraftVentoryClickEvent;
import com.github.syr0ws.craftventory.api.inventory.item.InventoryItem;
import com.github.syr0ws.craftventory.common.inventory.action.CommonAction;
import com.github.syr0ws.minewaypoints.cache.WaypointActivatedCache;
import com.github.syr0ws.minewaypoints.menu.data.CustomDataStoreKey;
import com.github.syr0ws.minewaypoints.menu.util.DataUtil;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.platform.BukkitWaypointActivationService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.Set;

public class ToggleWaypointActivation extends CommonAction {

    public static final String ACTION_NAME = "TOGGLE_WAYPOINT_ACTIVATION";

    private final Plugin plugin;
    private final BukkitWaypointActivationService waypointActivationService;

    public ToggleWaypointActivation(Set<ClickType> clickTypes, Plugin plugin, BukkitWaypointActivationService waypointActivationService) {
        super(clickTypes);

        Validate.notNull(plugin, "plugin cannot be null");
        Validate.notNull(waypointActivationService, "waypointActivationService cannot be null");

        this.plugin = plugin;
        this.waypointActivationService = waypointActivationService;
    }

    @Override
    public void execute(CraftVentoryClickEvent event) {

        CraftVentory inventory = event.getInventory();
        Player player = inventory.getViewer().getPlayer();
        Waypoint waypoint = this.getWaypoint(event);

        DataStore store = inventory.getLocalStore();

        WaypointActivatedCache cache = store.getData(CustomDataStoreKey.WAYPOINT_ACTIVATED_CACHE, WaypointActivatedCache.class)
                .orElseThrow(() -> new NullPointerException("WaypointActivatedCache not found in inventory local store"));

        // An item is always clicked when toggling a waypoint, so, it cannot be null.
        InventoryItem item = event.getItem().get();
        item.disable();

        Promise<BusinessResult<Waypoint, BusinessFailure>> promise = cache.isActivated(waypoint.getId()) ?
                this.waypointActivationService.deactivateWaypoint(player, waypoint.getId()) :
                this.waypointActivationService.activateWaypoint(player, waypoint.getId());

        promise.complete(() -> {
            // Inventory operations must be executed synchronously
            Bukkit.getScheduler().runTask(this.plugin, player::closeInventory);
        }).resolveAsync(this.plugin);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    private Waypoint getWaypoint(CraftVentoryClickEvent event) {

        Optional<Waypoint> waypointOptional = DataUtil.getContextualData(event, CustomDataStoreKey.WAYPOINT, Waypoint.class);

        if (waypointOptional.isPresent()) {
            return waypointOptional.get();
        }

        Optional<WaypointShare> waypointShareOptional = DataUtil.getContextualData(event, CustomDataStoreKey.WAYPOINT_SHARE, WaypointShare.class);

        if (waypointShareOptional.isPresent()) {
            return waypointShareOptional.get().getWaypoint();
        }

        throw new IllegalStateException("No waypoint found");
    }
}
