package com.github.syr0ws.minewaypoints.platform.spigot.menu.placeholder;

import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public enum WaypointPlaceholderEnum {

    ID(plugin -> new WaypointIdPlaceholder()),
    NAME(plugin -> new WaypointNamePlaceholder()),
    COORD_X(plugin -> new WaypointCoordXPlaceholder()),
    COORD_Y(plugin -> new WaypointCoordYPlaceholder()),
    COORD_Z(plugin -> new WaypointCoordZPlaceholder()),
    WORLD(plugin -> new WaypointWorldPlaceholder()),
    OWNER_NAME(plugin -> new WaypointOwnerNamePlaceholder()),
    OWNER_ID(plugin -> new WaypointOwnerIdPlaceholder()),
    CREATED_AT(WaypointCreatedAtPlaceholder::new);

    private final Function<Plugin, Placeholder> mapper;

    WaypointPlaceholderEnum(Function<Plugin, Placeholder> mapper) {
        this.mapper = mapper;
    }

    public Placeholder get(Plugin plugin) {
        return this.mapper.apply(plugin);
    }
}
