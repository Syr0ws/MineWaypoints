package com.github.syr0ws.minewaypoints.business.settings;

public record WaypointSettings(int waypointLimit) {

    public boolean hasWaypointLimit() {
        return this.waypointLimit != -1;
    }
}
