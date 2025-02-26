create table if not exists players (
    player_id varchar(60) primary key,
    player_name varchar(32) unique not null
);

create table if not exists waypoints (
    waypoint_id bigserial primary key,
    waypoint_name varchar(32) not null,
    icon varchar(128) not null,
    world varchar(128) not null,
    coord_x float not null,
    coord_y float not null,
    coord_z float not null,
    owner_id varchar(60) not null,
    created_at date not null,
    foreign key (owner_id) references players (player_id) on delete cascade,
    unique(waypoint_name, owner_id)
);

create table if not exists shared_waypoints (
    waypoint_id bigint,
    player_id varchar(60),
    shared_at date not null,
    primary key(waypoint_id, player_id),
    foreign key (player_id) references players (player_id) on delete cascade,
    foreign key (waypoint_id) references waypoints (waypoint_id) on delete cascade
);

create table if not exists activated_waypoints (
    waypoint_id bigint,
    player_id varchar(60),
    primary key (waypoint_id, player_id),
    foreign key (waypoint_id) references waypoints (waypoint_id) on delete cascade,
    foreign key (player_id) references players (player_id) on delete cascade
);
