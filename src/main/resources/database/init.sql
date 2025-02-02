PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS players (
    player_id VARCHAR(60) PRIMARY KEY,
    player_name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS waypoints (
    waypoint_id INTEGER PRIMARY KEY AUTOINCREMENT,
    waypoint_name VARCHAR(32) NOT NULL,
    icon VARCHAR(128) NOT NULL,
    world VARCHAR(128) NOT NULL,
    coord_x DOUBLE NOT NULL,
    coord_y DOUBLE NOT NULL,
    coord_z DOUBLE NOT NULL,
    owner_id VARCHAR(60) NOT NULL,
    created_at DATE NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES players (player_id) ON DELETE CASCADE,
    UNIQUE(waypoint_name, owner_id)
);

CREATE TABLE IF NOT EXISTS shared_waypoints (
    waypoint_id BIGINT,
    player_id VARCHAR(60),
    shared_at DATE NOT NULL,
    PRIMARY KEY(waypoint_id, player_id),
    FOREIGN KEY (player_id) REFERENCES players (player_id) ON DELETE CASCADE,
    FOREIGN KEY (waypoint_id) REFERENCES waypoints (waypoint_id) ON DELETE CASCADE
);