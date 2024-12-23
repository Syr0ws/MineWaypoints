package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.transform.placeholder.Placeholder;

public enum WaypointPlaceholderEnum {

    NAME(new WaypointNamePlaceholder()),
    COORD_X(new WaypointCoordXPlaceholder()),
    COORD_Y(new WaypointCoordYPlaceholder()),
    COORD_Z(new WaypointCoordZPlaceholder());

    private final Placeholder placeholder;

    WaypointPlaceholderEnum(Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    public Placeholder get() {
        return this.placeholder;
    }
}
