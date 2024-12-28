package com.github.syr0ws.minewaypoints.dao.jdbc;

import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.database.DatabaseConnection;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.Waypoint;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import org.bukkit.Material;

import java.sql.*;
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
            INSERT INTO waypoints (owner_id, name, icon, world, coord_x, coord_y, coord_z)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;

        try(PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, owner.getId().toString());
            statement.setString(2, name);
            statement.setString(3, icon.toString());
            statement.setString(4, location.getWorld());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.executeQuery();

            ResultSet resultSet = statement.getGeneratedKeys();

            if(!resultSet.next()) {
                throw new WaypointDataException("An error occurred while creating the waypoint");
            }

            long waypointId = resultSet.getLong("waypoint_id");
            Date createdAt = resultSet.getDate("created_at");

            return new Waypoint(waypointId, owner, createdAt, name, icon, location);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while creating the waypoint", exception);
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
            return this.getWaypointsFromResultSet(resultSet);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public List<Waypoint> findSharedWaypoints(UUID userId) throws WaypointDataException {

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
            return this.getWaypointsFromResultSet(resultSet);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public List<Waypoint> findAllWaypoints(UUID userId) throws WaypointDataException {

        List<Waypoint> waypoints = new ArrayList<>();

        waypoints.addAll(this.findWaypoints(userId));
        waypoints.addAll(this.findSharedWaypoints(userId));

        return waypoints;
    }

    private List<Waypoint> getWaypointsFromResultSet(ResultSet resultSet) throws SQLException {

        List<Waypoint> waypoints = new ArrayList<>();

        while(resultSet.next()) {

            // Waypoint data.
            long id = resultSet.getLong("waypoint_id");
            String name = resultSet.getString("waypoint_name");
            Material icon = Material.valueOf(resultSet.getString("icon"));
            Date createdAt = resultSet.getDate("creation_date");

            String world = resultSet.getString("world");
            double x = resultSet.getDouble("coord_x");
            double y = resultSet.getDouble("coord_y");
            double z = resultSet.getDouble("coord_z");
            WaypointLocation location = new WaypointLocation(world, x, y, z);

            // Waypoint owner data.
            UUID ownerId = UUID.fromString(resultSet.getString("owner_id"));
            String ownerName = resultSet.getString("player_name");

            WaypointUser owner = new WaypointUser(ownerId, ownerName);

            // Waypoint creation.
            waypoints.add(new Waypoint(id, owner, createdAt, name, icon, location));
        }

        return waypoints;
    }
}
