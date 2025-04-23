package com.github.syr0ws.minewaypoints.business.settings;

import java.util.Set;

public record WaypointSettings(int waypointLimit, Set<String> forbiddenWorlds) {

    public boolean hasWaypointLimit() {
        return this.waypointLimit != -1;
    }
}
