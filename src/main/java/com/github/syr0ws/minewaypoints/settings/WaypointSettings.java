package com.github.syr0ws.minewaypoints.settings;

import java.util.List;
import java.util.Set;

public record WaypointSettings(Set<String> forbiddenWorlds, List<WaypointLimitPermission> waypointCreationLimitPermissions) {

}
