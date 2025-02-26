-- Tables

pragma foreign_keys = on;

create table if not exists players
(
    player_id   varchar(60) primary key,
    player_name varchar(32) unique not null
);

create table if not exists waypoints
(
    waypoint_id   integer primary key autoincrement,
    waypoint_name varchar(32)  not null,
    icon          varchar(128) not null,
    world         varchar(128) not null,
    coord_x       double       not null,
    coord_y       double       not null,
    coord_z       double       not null,
    owner_id      varchar(60)  not null,
    created_at    date         not null,
    foreign key (owner_id) references players (player_id) on delete cascade,
    unique (waypoint_name, owner_id)
);

create table if not exists shared_waypoints
(
    waypoint_id integer,
    player_id   varchar(60),
    shared_at   date not null,
    primary key (waypoint_id, player_id),
    foreign key (player_id) references players (player_id) on delete cascade,
    foreign key (waypoint_id) references waypoints (waypoint_id) on delete cascade
);

create table if not exists activated_waypoints
(
    waypoint_id integer,
    player_id   varchar(60),
    primary key (waypoint_id, player_id),
    foreign key (waypoint_id) references waypoints (waypoint_id) on delete cascade,
    foreign key (player_id) references players (player_id) on delete cascade
);

-- Triggers

drop trigger if exists trigger_remove_activated_waypoint_when_unshare;

create trigger trigger_remove_activated_waypoint_when_unshare
    after delete
    on shared_waypoints
    for each row
begin
    delete
    from activated_waypoints
    where waypoint_id = old.waypoint_id
      and player_id = old.player_id;
end;
