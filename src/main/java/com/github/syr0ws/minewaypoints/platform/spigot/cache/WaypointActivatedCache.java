package com.github.syr0ws.minewaypoints.platform.spigot.cache;

import com.github.syr0ws.crafter.util.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WaypointActivatedCache {

    private final Set<Long> waypointIds;

    public WaypointActivatedCache(Set<Long> waypointIds) {
        Validate.notNull(waypointIds, "waypointIds cannot be null");
        this.waypointIds = Collections.unmodifiableSet(new HashSet<Long>(waypointIds));
    }

    public boolean isActivated(long waypointId) {
        return waypointIds.contains(waypointId);
    }

    public Set<Long> getWaypointIds() {
        return this.waypointIds;
    }
}
