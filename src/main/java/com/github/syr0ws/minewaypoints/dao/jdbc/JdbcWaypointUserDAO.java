package com.github.syr0ws.minewaypoints.dao.jdbc;

import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.database.DatabaseConnection;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JdbcWaypointUserDAO implements WaypointUserDAO {

    private final DatabaseConnection databaseConnection;

    public JdbcWaypointUserDAO(DatabaseConnection databaseConnection) {

        if(databaseConnection == null) {
            throw new IllegalArgumentException("databaseConnection cannot be null");
        }

        this.databaseConnection = databaseConnection;
    }

    @Override
    public WaypointUser createUser(UUID userId, String name) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = "INSERT INTO players (player_id, player_name) VALUES (?, ?)";

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());
            statement.setString(2, name);
            statement.executeUpdate();

            return this.findUser(userId);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while checking if the user exists", exception);
        }
    }

    @Override
    public boolean userExists(UUID userId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = "SELECT COUNT(1) FROM players WHERE player_id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt(1) == 1;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while checking if the user exists", exception);
        }
    }

    @Override
    public WaypointUser findUser(UUID userId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = "SELECT * FROM players WHERE player_id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {
                throw new WaypointDataException("User not found");
            }

            String name = resultSet.getString("player_name");

            Set<Long> waypointIds = this.findWaypoints(userId);
            Set<WaypointShare> sharedWaypoint = this.findSharedWaypoints(userId);

            return new WaypointUser(userId, name, waypointIds, sharedWaypoint);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading the user", exception);
        }
    }

    private Set<Long> findWaypoints(UUID userId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = "SELECT waypoint_id FROM waypoints WHERE player_id = ?;";

        Set<Long> waypointIds = new HashSet<>();

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                long waypointId = resultSet.getLong("waypoint_id");
                waypointIds.add(waypointId);
            }

            return waypointIds;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    private Set<WaypointShare> findSharedWaypoints(UUID userId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = "SELECT * FROM shared_waypoints WHERE player_id = ?;";

        Set<WaypointShare> sharedWaypoints = new HashSet<>();

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                long waypointId = resultSet.getLong("waypoint_id");
                Date sharedAt = resultSet.getDate("shared_at");
                sharedWaypoints.add(new WaypointShare(waypointId, sharedAt));
            }

            return sharedWaypoints;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's shared waypoints", exception);
        }
    }
}
