package com.github.syr0ws.minewaypoints.dao.jdbc;

import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.dao.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.database.DatabaseConnection;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class JdbcWaypointUserDAO implements WaypointUserDAO {

    private final DatabaseConnection databaseConnection;
    private final WaypointDAO waypointDAO;

    public JdbcWaypointUserDAO(DatabaseConnection databaseConnection, WaypointDAO waypointDAO) {

        if(databaseConnection == null) {
            throw new IllegalArgumentException("databaseConnection cannot be null");
        }

        if(waypointDAO == null) {
            throw new IllegalArgumentException("waypointDAO cannot be null");
        }

        this.databaseConnection = databaseConnection;
        this.waypointDAO = waypointDAO;
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

            List<Waypoint> waypointIds = this.waypointDAO.findWaypoints(userId);
            List<WaypointShare> sharedWaypoint = this.waypointDAO.findSharedWaypoints(userId);

            return new WaypointUser(userId, name, waypointIds, sharedWaypoint);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading the user", exception);
        }
    }
}