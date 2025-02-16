package com.github.syr0ws.minewaypoints.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class DirectionUtil {

    public static Direction getDirectionTo(Location current, Location to) {

        // The vector between the current location and the target one.
        Vector vector = getVectorBetween(current, to);

        // The target location with a yaw to the player location.
        Location targetLocation = to.clone().setDirection(vector);

        float yaw = current.getYaw();

        // Angle = playerYaw - targetYaw
        // Using modulo to set the angle between 0 and 360.
        double angle = ((yaw - targetLocation.getYaw()) % 360);

        return DirectionUtil.getDirection(angle);
    }

    private static Vector getVectorBetween(Location loc1, Location loc2) {

        // The vector between two locations can be found with the following formula : (x2 - x1 ; y2 - y1)
        // Normalizing the vector to get it with a norm of 1.
        return loc1.subtract(loc2).toVector().normalize();
    }

    private static Direction getDirection(double angle) {

        // If the angle is negative, modifying it to get a positive number.
        // This doesn't change the angle.
        if(angle < 0) angle += 360;

        if (0 <= angle && angle < 22.5) {
            return Direction.SOUTH;
        } else if (angle < 67.5) {
            return Direction.SOUTH_EAST;
        } else if (angle < 112.5) {
            return Direction.EAST;
        } else if (angle < 157.5) {
            return Direction.NORTH_EAST;
        } else if (angle < 202.5) {
            return Direction.NORTH;
        } else if (angle < 247.5) {
            return Direction.NORTH_WEST;
        } else if (angle < 292.5) {
            return Direction.WEST;
        } else if (angle < 337.5) {
            return Direction.SOUTH_WEST;
        } else if (angle < 360.0) {
            return Direction.SOUTH;
        } else {
            return Direction.NORTH;
        }
    }
}
