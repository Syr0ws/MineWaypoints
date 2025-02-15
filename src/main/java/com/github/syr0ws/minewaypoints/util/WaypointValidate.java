package com.github.syr0ws.minewaypoints.util;

public class WaypointValidate {

    public static boolean isValidWaypointName(String waypointName) {
        return waypointName != null && !waypointName.isEmpty() && waypointName.length() <= 32;
    }

    public static void checkWaypointName(String waypointName) {
        if (!isValidWaypointName(waypointName)) {
            throw new IllegalArgumentException(String.format("Invalid waypoint name '%s'", waypointName));
        }
    }
}
