package com.github.syr0ws.minewaypoints.dao.jdbc;

import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.database.DatabaseConnection;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointShare;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JdbcWaypointDAO implements WaypointDAO {

    private final DatabaseConnection databaseConnection;

    public JdbcWaypointDAO(DatabaseConnection databaseConnection) {

        if(databaseConnection == null) {
            throw new IllegalArgumentException("databaseConnection cannot be null");
        }

        this.databaseConnection = databaseConnection;
    }

    @Override
    public Waypoint createWaypoint(WaypointUser owner, String name, Material icon, WaypointLocation location) throws WaypointDataException {

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

            return new Waypoint(waypointId, owner, createdAt, name, icon, location);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while creating the waypoint", exception);
        }
    }

    @Override
    public Waypoint findWaypoint(long waypointId) throws WaypointDataException {

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
                throw new WaypointDataException("Waypoint not found");
            }

            return this.getWaypointFromResultSet(resultSet);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public void updateWaypoint(Waypoint waypoint) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = """
            UPDATE waypoints SET name = ?, icon = ?, world = ?, coord_x = ?, coord_y = ?, coord_z = ?
                WHERE waypoint_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            WaypointLocation location = waypoint.getLocation();

            statement.setString(2, waypoint.getName());
            statement.setString(3, waypoint.getIcon().toString());
            statement.setString(4, location.getWorld());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.executeQuery();

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
    public WaypointShare shareWaypoint(WaypointUser to, long waypointId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = """
            INSERT INTO shared_waypoints (waypoint_id, player_id, shared_at) VALUES (?, ?, ?)
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            Date sharedAt = new Date();

            statement.setLong(1, waypointId);
            statement.setString(2, to.getId().toString());
            statement.setDate(3, new java.sql.Date(sharedAt.getTime()));
            statement.executeQuery();

            Waypoint waypoint = this.findWaypoint(waypointId);;

            return new WaypointShare(waypoint, sharedAt);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while sharing the waypoint", exception);
        }
    }

    @Override
    public void unshareWaypoint(WaypointUser from, long waypointId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();

        String query = """
            DELETE FROM WHERE waypoint_id = ? AND player_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, from.getId().toString());
            statement.executeQuery();

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while unsharing the waypoint", exception);
        }
    }

    @Override
    public List<Waypoint> findWaypoints(UUID userId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = """
            SELECT * 
                FROM waypoints as w 
                JOIN players as p ON w.owner_id = p.player_id 
                WHERE w.owner_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            List<Waypoint> waypoints = new ArrayList<>();

            while(resultSet.next()) {

                Waypoint waypoint = this.getWaypointFromResultSet(resultSet);
                waypoints.add(waypoint);
            }

            return waypoints;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public List<WaypointShare> findSharedWaypoints(UUID userId) throws WaypointDataException {

        Connection connection = this.databaseConnection.getConnection();
        String query = """
            SELECT * 
                FROM waypoints as w 
                JOIN players as p ON w.owner_id = p.player_id 
                JOIN shared_waypoints as sw ON w.waypoint_id = sw.waypoint_id
                WHERE w.owner_id = ?;
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            List<WaypointShare> sharedWaypoints = new ArrayList<>();

            while(resultSet.next()) {

                Waypoint waypoint = this.getWaypointFromResultSet(resultSet);
                Date sharedAt = resultSet.getDate("shared_at");

                WaypointShare share = new WaypointShare(waypoint, sharedAt);
                sharedWaypoints.add(share);
            }

            return sharedWaypoints;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    private Waypoint getWaypointFromResultSet(ResultSet resultSet) throws SQLException {

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

        WaypointUser owner = new WaypointUser(ownerId, ownerName);

        return new Waypoint(id, owner, createdAt, name, icon, location);
    }
}
