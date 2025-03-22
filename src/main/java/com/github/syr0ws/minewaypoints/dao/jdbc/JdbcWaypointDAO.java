package com.github.syr0ws.minewaypoints.dao.jdbc;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.minewaypoints.dao.WaypointDAO;
import com.github.syr0ws.minewaypoints.database.connection.DatabaseConnection;
import com.github.syr0ws.minewaypoints.exception.WaypointDataException;
import com.github.syr0ws.minewaypoints.model.WaypointLocation;
import com.github.syr0ws.minewaypoints.model.WaypointUser;
import com.github.syr0ws.minewaypoints.model.entity.WaypointEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointShareEntity;
import com.github.syr0ws.minewaypoints.model.entity.WaypointUserEntity;

import java.sql.*;
import java.util.Date;
import java.util.*;

public class JdbcWaypointDAO implements WaypointDAO {

    private final DatabaseConnection databaseConnection;

    public JdbcWaypointDAO(DatabaseConnection databaseConnection) {
        Validate.notNull(databaseConnection, "databaseConnection cannot be null");

        this.databaseConnection = databaseConnection;
    }

    @Override
    public WaypointEntity createWaypoint(WaypointUser owner, String name, String icon, WaypointLocation location) throws WaypointDataException {
        Validate.notNull(owner, "owner cannot be null");
        Validate.notNull(name, "name cannot be null");
        Validate.notEmpty(icon, "icon cannot be null or empty");
        Validate.notNull(location, "location cannot be null");

        String query = """
                insert into waypoints (owner_id, waypoint_name, icon, world, coord_x, coord_y, coord_z, created_at)
                    values (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            Date createdAt = new Date();

            statement.setString(1, owner.getId().toString());
            statement.setString(2, name);
            statement.setString(3, icon);
            statement.setString(4, location.getWorld());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.setDate(8, new java.sql.Date(createdAt.getTime()));
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();

            if (!resultSet.next()) {
                String message = String.format("An error occurred retrieving the id of the create waypoint for player %s", owner.getId());
                throw new WaypointDataException(message);
            }

            long waypointId = resultSet.getLong(1);

            return new WaypointEntity(waypointId, owner, createdAt, name, icon, location);

        } catch (SQLException exception) {
            String message = String.format("An error occurred while creating a waypoint for player %s", owner.getId());
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public void updateWaypoint(WaypointEntity waypoint) throws WaypointDataException {
        Validate.notNull(waypoint, "waypoint cannot be null");

        String query = """
                update waypoints set waypoint_name = ?, icon = ?, world = ?, coord_x = ?, coord_y = ?, coord_z = ?
                    where waypoint_id = ?;""";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            WaypointLocation location = waypoint.getLocation();

            statement.setString(1, waypoint.getName());
            statement.setString(2, waypoint.getIcon());
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

        String query = "delete from waypoints where waypoint_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while deleting the waypoint", exception);
        }
    }

    @Override
    public boolean hasWaypointByName(UUID ownerId, String waypointName) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notNull(waypointName, "waypointName cannot be null");

        String query = "select count(1) from waypoints where owner_id = ? and waypoint_name = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ownerId.toString());
            statement.setString(2, waypointName);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt(1) == 1;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while checking if a user has a waypoint by name", exception);
        }
    }

    @Override
    public boolean hasAccessToWaypoint(UUID playerId, long waypointId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");

        String query = """
                select waypoint_id from waypoints as w where w.waypoint_id = ? and w.owner_id = ?
                union
                select waypoint_id from shared_waypoints as sw where sw.waypoint_id = ? and sw.player_id = ?;""";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, playerId.toString());
            statement.setLong(3, waypointId);
            statement.setString(4, playerId.toString());

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException exception) {
            String message = String.format("An error occurred while checking access to the waypoint %d for player %s", waypointId, playerId);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public int countWaypoints(UUID ownerId) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");

        String query = "select count(*) from waypoint_view as wv where owner_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ownerId.toString());

            ResultSet resultSet = statement.executeQuery();

            return resultSet.getInt(1);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while counting user's waypoints", exception);
        }
    }

    @Override
    public Optional<WaypointEntity> findWaypointById(long waypointId) throws WaypointDataException {

        String query = "select wv.* from waypoint_view as wv where waypoint_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(this.getWaypointFromResultSet(resultSet));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while retrieving a waypoint by id", exception);
        }
    }

    @Override
    public Optional<WaypointEntity> findWaypointByOwnerAndId(UUID ownerId, long waypointId) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");

        String query = "select wv.* from waypoint_view as wv where wv.owner_id = ? and wv.waypoint_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ownerId.toString());
            statement.setLong(2, waypointId);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(this.getWaypointFromResultSet(resultSet));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while retrieving a waypoint by owner and id", exception);
        }
    }

    @Override
    public Optional<WaypointEntity> findWaypointByOwnerAndName(UUID ownerId, String waypointName) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");
        Validate.notNull(ownerId, "waypointName cannot be null");

        String query = "select wv.* from waypoint_view as wv where wv.owner_id = ? and waypoint_name = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ownerId.toString());
            statement.setString(2, waypointName);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(this.getWaypointFromResultSet(resultSet));

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while retrieving a waypoint by owner and id", exception);
        }
    }

    @Override
    public List<WaypointEntity> findWaypoints(UUID ownerId) throws WaypointDataException {
        Validate.notNull(ownerId, "ownerId cannot be null");

        String query = "select wv.* from waypoint_view as wv where owner_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ownerId.toString());

            ResultSet resultSet = statement.executeQuery();

            List<WaypointEntity> waypoints = new ArrayList<>();

            while (resultSet.next()) {
                WaypointEntity waypoint = this.getWaypointFromResultSet(resultSet);
                waypoints.add(waypoint);
            }

            return waypoints;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while retrieving user's waypoints", exception);
        }
    }

    @Override
    public WaypointShareEntity shareWaypoint(long waypointId, UUID targetId) throws WaypointDataException {
        Validate.notNull(targetId, "targetId cannot be null");

        String query = "insert into shared_waypoints (waypoint_id, player_id, shared_at) values (?, ?, ?);";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            Date sharedAt = new Date();

            statement.setLong(1, waypointId);
            statement.setString(2, targetId.toString());
            statement.setDate(3, new java.sql.Date(sharedAt.getTime()));
            statement.executeUpdate();

            return this.findWaypointShare(waypointId, targetId).orElseThrow();

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while sharing the waypoint", exception);
        }
    }

    @Override
    public boolean unshareWaypoint(long waypointId, UUID targetId) throws WaypointDataException {
        Validate.notNull(targetId, "targetId cannot be null");

        String query = "delete from shared_waypoints where waypoint_id = ? and player_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, targetId.toString());
            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while unsharing the waypoint", exception);
        }
    }

    @Override
    public boolean isShared(long waypointId, String username) throws WaypointDataException {

        String query = """
                select count(1)
                from waypoint_share_view
                where waypoint_id = ? and shared_with_name = ?;""";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, username);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt(1) == 1;

        } catch (SQLException exception) {
            String message = String.format("An error occurred while checking if a waypoint is shared with the player %s", username);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public boolean isShared(long waypointId, UUID targetId) throws WaypointDataException {

        String query = """
                select count(1)
                from waypoint_share_view
                where waypoint_id = ? and shared_with_id = ?;""";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, targetId.toString());

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt(1) == 1;

        } catch (SQLException exception) {
            String message = String.format("An error occurred while checking if a waypoint is shared with the player %s", targetId);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public Optional<WaypointShareEntity> findWaypointShare(long waypointId, UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null or empty");

        String query = "select * from waypoint_share_view where waypoint_id = ? and shared_with_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, playerId.toString());

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            WaypointShareEntity share = this.getWaypointShareFromResultSet(resultSet);

            return Optional.of(share);

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public List<WaypointShareEntity> findSharedWaypoints(UUID userId) throws WaypointDataException {
        Validate.notNull(userId, "userId cannot be null");

        String query = "select * from waypoint_share_view where shared_with_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userId.toString());

            ResultSet resultSet = statement.executeQuery();

            List<WaypointShareEntity> sharedWaypoints = new ArrayList<>();

            while (resultSet.next()) {
                WaypointShareEntity share = this.getWaypointShareFromResultSet(resultSet);
                sharedWaypoints.add(share);
            }

            return sharedWaypoints;

        } catch (SQLException exception) {
            throw new WaypointDataException("An error occurred while loading user's waypoints", exception);
        }
    }

    @Override
    public List<WaypointShareEntity> findSharedWith(long waypointId) throws WaypointDataException {
        String query = "select * from waypoint_share_view where waypoint_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);

            ResultSet resultSet = statement.executeQuery();

            List<WaypointShareEntity> sharedWaypoints = new ArrayList<>();

            while (resultSet.next()) {
                WaypointShareEntity share = this.getWaypointShareFromResultSet(resultSet);
                sharedWaypoints.add(share);
            }

            return sharedWaypoints;

        } catch (SQLException exception) {
            String message = String.format("An error occurred while retrieving players the waypoint %d has been shared with", waypointId);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public void activateWaypoint(long waypointId, UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");

        String query = "insert into activated_waypoints (waypoint_id, player_id) values (?, ?);";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();

        } catch (SQLException exception) {
            String message = String.format("An error occurred while activating a waypoint for player %s", playerId);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public void deactivateWaypoint(long waypointId, UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");

        String query = """
                delete from activated_waypoints where waypoint_id = ? and player_id = ?;
                """;

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, waypointId);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();

        } catch (SQLException exception) {
            String message = String.format("An error occurred while deactivating a waypoint for player %s", playerId);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public void deactivateWaypoint(UUID playerId, String world) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        Validate.notNull(world, "world cannot be null");

        // We are doing two requests here because it is the only way to make a query
        // that is supported by all the supported databases.
        Optional<Long> optional = this.getActivatedWaypointIdByWorld(playerId, world);

        if(optional.isEmpty()) {
            return;
        }

        long waypointId = optional.get();

        String query = "delete from activated_waypoints where player_id = ? and waypoint_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, playerId.toString());
            statement.setLong(2, waypointId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            String message = String.format("An error occurred while deactivating a waypoint for player %s", playerId);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public Optional<WaypointEntity> findActivatedWaypoint(UUID playerId, String world) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");
        Validate.notNull(world, "world cannot be null");

        String query = """
                select *
                    from activated_waypoints as aw
                    join waypoint_view as wv on aw.waypoint_id = wv.waypoint_id
                    where aw.player_id = ? and wv.world = ?;
                """;

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, playerId.toString());
            statement.setString(2, world);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            WaypointEntity waypoint = this.getWaypointFromResultSet(resultSet);

            return Optional.of(waypoint);

        } catch (SQLException exception) {
            String message = String.format("An error occurred while retrieving activated waypoint for player %s", playerId);
            throw new WaypointDataException(message, exception);
        }
    }

    @Override
    public Set<Long> getActivatedWaypointIds(UUID playerId) throws WaypointDataException {
        Validate.notNull(playerId, "playerId cannot be null");

        String query = "select waypoint_id from activated_waypoints where player_id = ?;";

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, playerId.toString());

            ResultSet resultSet = statement.executeQuery();
            HashSet<Long> waypointIds = new HashSet<>();

            while(resultSet.next()) {
                waypointIds.add(resultSet.getLong("waypoint_id"));
            }

            return waypointIds;

        } catch (SQLException exception) {
            String message = String.format("An error occurred while retrieving activated waypoint ids for player %s", playerId);
            throw new WaypointDataException(message, exception);
        }
    }

    private WaypointEntity getWaypointFromResultSet(ResultSet resultSet) throws SQLException {

        // Waypoint data.
        long id = resultSet.getLong("waypoint_id");
        String name = resultSet.getString("waypoint_name");
        String icon = resultSet.getString("icon");
        Date createdAt = resultSet.getDate("created_at");

        String world = resultSet.getString("world");
        double x = resultSet.getDouble("coord_x");
        double y = resultSet.getDouble("coord_y");
        double z = resultSet.getDouble("coord_z");
        WaypointLocation location = new WaypointLocation(world, x, y, z);

        // Waypoint owner data.
        UUID ownerId = UUID.fromString(resultSet.getString("owner_id"));
        String ownerName = resultSet.getString("owner_name");

        WaypointUserEntity owner = new WaypointUserEntity(ownerId, ownerName);

        return new WaypointEntity(id, owner, createdAt, name, icon, location);
    }

    private WaypointShareEntity getWaypointShareFromResultSet(ResultSet resultSet) throws SQLException {

        // Retrieving the waypoint.
        WaypointEntity waypoint = this.getWaypointFromResultSet(resultSet);

        // Retrieving the user the waypoint is shared with.
        UUID shared_with_id = UUID.fromString(resultSet.getString("shared_with_id"));
        String shared_with_name = resultSet.getString("shared_with_name");
        WaypointUser sharedWith = new WaypointUserEntity(shared_with_id, shared_with_name);

        // Retrieving share data.
        Date sharedAt = resultSet.getDate("shared_at");

        return new WaypointShareEntity(sharedWith, waypoint, sharedAt);
    }

    private Optional<Long> getActivatedWaypointIdByWorld(UUID playerId, String world) throws WaypointDataException {

        String query1 = """
            select w.waypoint_id
                from activated_waypoints as aw
                join waypoints as w on w.waypoint_id = aw.waypoint_id
                where aw.player_id = ? and w.world = ?
                limit 1;
        """;

        try (Connection connection = this.databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query1)) {

            statement.setString(1, playerId.toString());
            statement.setString(2, world);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() ? Optional.of(resultSet.getLong("waypoint_id")) : Optional.empty();

        } catch (SQLException exception) {
            String message = String.format("An error occurred while retrieving an activated waypoint by world for player %s", playerId);
            throw new WaypointDataException(message, exception);
        }
    }
}
