package com.github.syr0ws.minewaypoints.service;

import com.github.syr0ws.crafter.util.Promise;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.service.util.WaypointEnums;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface WaypointActivationService {

    /**
     * Activates a waypoint for a given player.
     *
     * <p>This method ensures that the player has access to the waypoint before activation. If the player
     * has another waypoint activated in the same world, it will be deactivated before activating the
     * new waypoint.</p>
     *
     * @param player   The {@link Player} attempting to activate the waypoint. Cannot be null.
     * @param waypointId The id of the waypoint to be activated.
     * @return A {@code Promise} resolving to a {@link WaypointEnums.WaypointActivationStatus} indicating the result:
     *         <ul>
     *           <li>{@code WAYPOINT_NOT_FOUND} - If the waypoint does not exist.</li>
     *           <li>{@code NO_WAYPOINT_ACCESS} - If the player does not have access to the waypoint.</li>
     *           <li>{@code ACTIVATED} - If the waypoint has been successfully activated.</li>
     *         </ul>
     * @throws IllegalArgumentException if {@code playerId} is null.
     */
    Promise<WaypointEnums.WaypointActivationStatus> activateWaypoint(Player player, long waypointId);

    /**
     * Deactivates a waypoint for a given player.
     *
     * <p>This method will not fail if the waypoint does not exist or if it is not activated for the given player.</p>
     *
     * @param player   The {@link Player} for which deactivate the waypoint. Cannot be null.
     * @param waypointId The id of the waypoint to be deactivated.
     * @return A {@code Promise} that resolves when the waypoint has been deactivated.
     * @throws IllegalArgumentException if {@code playerId} is null.
     */
    Promise<Void> deactivateWaypoint(Player player, long waypointId);

    Promise<Optional<Waypoint>> getActivatedWaypoint(Player player, String world);

    void showWaypoint(Player player, Waypoint waypoint);

    void hideWaypoint(Player player);

    void hideAll();
}
