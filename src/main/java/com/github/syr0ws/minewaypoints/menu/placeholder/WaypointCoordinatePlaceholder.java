package com.github.syr0ws.minewaypoints.menu.placeholder;

import com.github.syr0ws.craftventory.api.util.Context;

import java.text.DecimalFormat;

public abstract class WaypointCoordinatePlaceholder extends WaypointPlaceholder {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    protected abstract double getCoordiate(Context context);

    @Override
    public String getValue(Context context) {
        double coordinate = this.getCoordiate(context);
        return DECIMAL_FORMAT.format(coordinate);
    }
}
