package com.github.syr0ws.minewaypoints.infrastructure.persistence.jdbc;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.plugin.persistence.WaypointDAO;
import com.github.syr0ws.minewaypoints.plugin.persistence.WaypointUserDAO;
import com.github.syr0ws.minewaypoints.infrastructure.persistence.database.connection.DatabaseConnection;
import com.github.syr0ws.minewaypoints.plugin.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.plugin.domain.entity.WaypointUserEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcWaypointUserDAO implements WaypointUserDAO {

    private final DatabaseConnection databaseConnection;
    private final WaypointDAO waypointDAO;

    public JdbcWaypointUserDAO(DatabaseConnection databaseConnection, WaypointDAO waypointDAO) {
        Validate.notNull(databaseConnection, "databaseConnection cannot be null");
        Validate.notNull(waypointDAO, "waypointDAO cannot be null");

        this.databaseConnection = databaseConnection;
        this.waypointDAO = waypointDAO;
    }

    @Override
    public WaypointOwnerEntity createUser(UUID userId, String name) throws WaypointDataException {
        Validate.notNull(userId, "userId cannot be null");
        Validate.notNull(name, "name cannot be null");

        String query = "insert into players (player_id, player_name) values (?, ?);";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());
            statement.setString(2, name);
            statement.executeUpdate();

            return this.findOwnerById(userId).orElseThrow(() -> new WaypointDataException("User not found"));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while creating the player", exception);
        }
    }

    @Override
    public boolean userExists(UUID userId) throws WaypointDataException {
        Validate.notNull(userId, "userId cannot be null");

        String query = "select count(1) from players where player_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt(1) == 1;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while checking if the player exists", exception);
        }
    }

    @Override
    public Optional<WaypointOwnerEntity> findOwnerById(UUID userId) throws WaypointDataException {
        Validate.notNull(userId, "userId cannot be null");

        String query = "select * from players where player_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            String name = resultSet.getString("player_name");

            List<WaypointEntity> waypoints = this.waypointDAO.findWaypoints(userId);

            return Optional.of(new WaypointOwnerEntity(userId, name, waypoints));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading the player's data", exception);
        }
    }

    @Override
    public Optional<WaypointUserEntity> findUserById(UUID userId) throws WaypointDataException {
        Validate.notNull(userId, "userId cannot be null");

        String query = "select * from players where player_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            String name = resultSet.getString("player_name");

            return Optional.of(new WaypointUserEntity(userId, name));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while retrieving the player by id", exception);
        }
    }

    @Override
    public Optional<WaypointUserEntity> findUserByName(String username) throws WaypointDataException {
        Validate.notNull(username, "username cannot be null");

        String query = "select * from players where player_name = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            UUID playerId = UUID.fromString(resultSet.getString("player_id"));

            return Optional.of(new WaypointUserEntity(playerId, username));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while retrieving the player by name", exception);
        }
    }
}
