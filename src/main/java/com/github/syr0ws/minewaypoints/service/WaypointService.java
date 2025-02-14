package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.service.util.WaypointEnums;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaypointService {

    Promise<Waypoint> createWaypoint(UUID ownerId, String name, Material icon, Location location);

    Promise<Void> updateWaypointIcon(long waypointId, Material icon);

    Promise<Void> updateWaypointName(long waypointId, String newName);

    Promise<Void> updateWaypointLocation(long waypointId, Location location);

    Promise<Void> deleteWaypoint(long waypointId);

    Promise<WaypointShare> shareWaypoint(String targetName, long waypointId);

    Promise<Boolean> unshareWaypoint(String targetName, long waypointId);

    /**
     * Activates a waypoint for a given player.
     *
     * <p>This method ensures that the player has access to the waypoint before activation. If the player
     * has another waypoint activated in the same world, it will be deactivated before activating the
     * new waypoint.</p>
     *
     * @param playerId   The UUID of the player attempting to activate the waypoint. Cannot be null.
     * @param waypointId The id of the waypoint to be activated.
     * @return A {@code Promise} resolving to a {@link WaypointEnums.WaypointActivationStatus} indicating the result:
     *         <ul>
     *           <li>{@code WAYPOINT_NOT_FOUND} - If the waypoint does not exist.</li>
     *           <li>{@code NO_WAYPOINT_ACCESS} - If the player does not have access to the waypoint.</li>
     *           <li>{@code ACTIVATED} - If the waypoint has been successfully activated.</li>
     *         </ul>
     * @throws IllegalArgumentException if {@code playerId} is null.
     */
    Promise<WaypointEnums.WaypointActivationStatus> activateWaypoint(UUID playerId, long waypointId);

    /**
     * Deactivates a waypoint for a given player.
     *
     * <p>This method will not fail if the waypoint does not exist or if it is not activated for the given player.</p>
     *
     * @param playerId   The UUID of the player for which deactivate the waypoint. Cannot be null.
     * @param waypointId The id of the waypoint to be deactivated.
     * @return A {@code Promise} that resolves when the waypoint has been deactivated.
     * @throws IllegalArgumentException if {@code playerId} is null.
     */
    Promise<Void> deactivateWaypoint(UUID playerId, long waypointId);

    Promise<Optional<Waypoint>> getActivatedWaypoint(UUID userId, String world);

    Promise<List<WaypointShare>> getSharedWaypoints(UUID userId);

    Promise<List<WaypointShare>> getSharedWith(long waypointId);
}
