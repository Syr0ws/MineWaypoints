package com.github.syr0ws.minewaypoints.dao.jdbc;

import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.database.DatabaseConnection;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointOwnerEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointShareEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class JdbcWaypointDAO implements WaypointDAO {

    private final DatabaseConnection databaseConnection;

    public JdbcWaypointDAO(DatabaseConnection databaseConnection) {

        if(databaseConnection == null) {
            throw new IllegalArgumentException("databaseConnection cannot be null");
        }

        this.databaseConnection = databaseConnection;
    }

    @Override
    public WaypointEntity createWaypoint(WaypointOwnerEntity owner, String name, Material icon, WaypointLocation location) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = """
            INSERT INTO waypoints (owner_id, waypoint_name, icon, world, coord_x, coord_y, coord_z, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            Date createdAt = new Date();

            statement.setString(1, owner.getId().toString());
            statement.setString(2, name);
            statement.setString(3, icon.toString());
            statement.setString(4, location.getWorld());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.setDate(8, new java.sql.Date(createdAt.getTime()));
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();

            if(!resultSet.next()) {
                throw new WaypointDataException("An error occurred while creating the waypoint");
            }

            long waypointId = resultSet.getLong(1);

            return new WaypointEntity(waypointId, owner, createdAt, name, icon, location);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while creating the waypoint", exception);
        }
    }

    @Override
    public Optional<WaypointEntity> findWaypoint(long waypointId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = """
            SELECT * 
                FROM waypoints as w 
                JOIN players as p ON w.owner_id = p.player_id 
                WHERE w.waypoint_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);

            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(this.getWaypointFromResultSet(resultSet));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public boolean hasWaypointByName(UUID ownerId, String name) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = "SELECT COUNT(1) FROM waypoints WHERE waypoint_id = ? AND name = ?;";

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ownerId.toString());
            statement.setString(2, name);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt(1) == 1;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public void updateWaypoint(WaypointEntity waypoint) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = """
            UPDATE waypoints SET waypoint_name = ?, icon = ?, world = ?, coord_x = ?, coord_y = ?, coord_z = ?
                WHERE waypoint_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            WaypointLocation location = waypoint.getLocation();

            statement.setString(1, waypoint.getName());
            statement.setString(2, waypoint.getIcon().toString());
            statement.setString(3, location.getWorld());
            statement.setDouble(4, location.getX());
            statement.setDouble(5, location.getY());
            statement.setDouble(6, location.getZ());
            statement.setLong(7, waypoint.getId());
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while updating the waypoint", exception);
        }
    }

    @Override
    public void deleteWaypoint(long waypointId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = "DELETE FROM waypoints WHERE waypoint_id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while creating the waypoint", exception);
        }
    }

    @Override
    public WaypointShareEntity shareWaypoint(WaypointUserEntity to, WaypointEntity waypoint) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = "INSERT INTO shared_waypoints (waypoint_id, player_id, shared_at) VALUES (?, ?, ?)";

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            Date sharedAt = new Date();

            statement.setLong(1, waypoint.getId());
            statement.setString(2, to.getId().toString());
            statement.setDate(3, new java.sql.Date(sharedAt.getTime()));
            statement.executeQuery();

            return new WaypointShareEntity(to, waypoint, sharedAt);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while sharing the waypoint", exception);
        }
    }

    @Override
    public boolean unshareWaypoint(String username, long waypointId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = """
            DELETE FROM shared_waypoints AS sw JOIN players AS p ON sw.player_id = p.player_id WHERE sw.waypoint_id = ? AND p.player_name = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, username);
            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while unsharing the waypoint", exception);
        }
    }

    @Override
    public List<WaypointEntity> findWaypoints(UUID ownerId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = """
            SELECT * 
                FROM waypoints as w 
                JOIN players as p ON w.owner_id = p.player_id 
                WHERE w.owner_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ownerId.toString());

            ResultSet resultSet = statement.executeQuery();

            List<WaypointEntity> waypoints = new ArrayList<>();

            while(resultSet.next()) {

                WaypointEntity waypoint = this.getWaypointFromResultSet(resultSet);
                waypoints.add(waypoint);
            }

            return waypoints;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public List<WaypointShareEntity> findSharedWaypoints(UUID userId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = """
            SELECT * 
                FROM shared_waypoints as sw 
                JOIN waypoints as w ON w.waypoint_id = sw.waypoint_id
                JOIN players as p ON w.owner_id = p.player_id 
                WHERE w.owner_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            List<WaypointShareEntity> sharedWaypoints = new ArrayList<>();

            while(resultSet.next()) {

                // Retrieving the waypoint.
                WaypointEntity waypoint = this.getWaypointFromResultSet(resultSet);

                // Retrieving the user the waypoint is shared with.
                UUID player_id = UUID.fromString(resultSet.getString("player_id"));
                String player_name = resultSet.getString("player_name");
                WaypointUser sharedWith = new WaypointUserEntity(player_id, player_name);

                // Retrieving share data.
                Date sharedAt = resultSet.getDate("shared_at");

                WaypointShareEntity share = new WaypointShareEntity(sharedWith, waypoint, sharedAt);
                sharedWaypoints.add(share);
            }

            return sharedWaypoints;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public List<WaypointShareEntity> findSharedWith(WaypointEntity waypoint) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = """
            SELECT w.waypoint_id, sw.shared_at, p.player_id, p.player_name
                FROM waypoints AS w
                JOIN waypoint_shares AS sw ON w.waypoint_id = w.waypoint_id
                JOIN players AS p ON sw.player_id = p.player_id
                WHERE w.waypoint_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypoint.getId());

            ResultSet resultSet = statement.executeQuery();

            List<WaypointShareEntity> sharedWaypoints = new ArrayList<>();

            while(resultSet.next()) {

                // Retrieving the user the waypoint is shared with.
                UUID player_id = UUID.fromString(resultSet.getString("player_id"));
                String player_name = resultSet.getString("player_name");
                WaypointUser sharedWith = new WaypointUserEntity(player_id, player_name);

                // Retrieving share data.
                Date sharedAt = resultSet.getDate("shared_at");

                WaypointShareEntity share = new WaypointShareEntity(sharedWith, waypoint, sharedAt);
                sharedWaypoints.add(share);
            }

            return sharedWaypoints;

        } catch (SQLException exception) {
            String message = String.format("An error occurred while retrieving players the waypoint %d has been shared with", waypoint.getId());
            throw new WaypointDataException(message, exception);
        }
    }

    private WaypointEntity getWaypointFromResultSet(ResultSet resultSet) throws SQLException {

        // Waypoint data.
        long id = resultSet.getLong("waypoint_id");
        String name = resultSet.getString("waypoint_name");
        Material icon = Material.valueOf(resultSet.getString("icon"));
        Date createdAt = resultSet.getDate("created_at");

        String world = resultSet.getString("world");
        double x = resultSet.getDouble("coord_x");
        double y = resultSet.getDouble("coord_y");
        double z = resultSet.getDouble("coord_z");
        WaypointLocation location = new WaypointLocation(world, x, y, z);

        // Waypoint owner data.
        UUID ownerId = UUID.fromString(resultSet.getString("owner_id"));
        String ownerName = resultSet.getString("player_name");

        WaypointUserEntity owner = new WaypointUserEntity(ownerId, ownerName);

        return new WaypointEntity(id, owner, createdAt, name, icon, location);
    }
}
