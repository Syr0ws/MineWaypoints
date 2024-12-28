CREATE TABLE IF NOT EXISTS players (
    player_id VARCHAR(60) PRIMARY KEY,
    player_name VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS waypoints (
    waypoint_id BIGINT PRIMARY_KEY,
    waypoint_name VARCHAR(32) NOT NULL,
    icon VARCHAR(128) NOT NULL,
    world VARCHAR(128) NOT NULL,
    coord_x DOUBLE NOT NULL,
    coord_y DOUBLE NOT NULL,
    coord_z DOUBLE NOT NULL,
    owner_id VARCHAR(60) NOT NULL,
    created_at DATE NOT NULL DEFAULT NOW(),
    FOREIGN KEY (owner_id) REFERENCES players (player_id),
    UNIQUE(waypoint_name, owner_id)
);

CREATE TABLE IF NOT EXISTS shared_waypoints (
    waypoint_id BIGINT,
    owner_id VARCHAR(60),
    shared_at DATE NOT NULL DEFAULT NOW(),
    PRIMARY KEY(waypoint_id, owner_id)
);