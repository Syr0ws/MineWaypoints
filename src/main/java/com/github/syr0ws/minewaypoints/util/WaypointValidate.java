package com.github.syr0ws.minewaypoints.util;

public class WaypointValidate {

    public static boolean isValidWaypointName(String waypointName) {
        return waypointName != null && !waypointName.isEmpty() && waypointName.length() <= 32;
    }
}
