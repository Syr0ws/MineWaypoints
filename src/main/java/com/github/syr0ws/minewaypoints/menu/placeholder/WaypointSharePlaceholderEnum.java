package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public enum WaypointSharePlaceholderEnum {

    TO_USER_NAME(plugin -> new WaypointSharedToUserNamePlaceholder()),
    SHARED_AT(WaypointSharedAtPlaceholder::new);

    private final Function<Plugin, Placeholder> mapper;

    WaypointSharePlaceholderEnum(Function<Plugin, Placeholder> mapper) {
        this.mapper = mapper;
    }

    public Placeholder get(Plugin plugin) {
        return this.mapper.apply(plugin);
    }
}
