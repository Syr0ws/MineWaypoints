-- Tables

create table if not exists players
(
    player_id   varchar(60) primary key,
    player_name varchar(32) unique not null
);

create table if not exists waypoints
(
    waypoint_id   bigserial primary key,
    waypoint_name varchar(32)  not null,
    icon          varchar(128) not null,
    world         varchar(128) not null,
    coord_x       float        not null,
    coord_y       float        not null,
    coord_z       float        not null,
    owner_id      varchar(60)  not null,
    created_at    date         not null,
    foreign key (owner_id) references players (player_id) on delete cascade,
    unique (waypoint_name, owner_id)
);

create table if not exists shared_waypoints
(
    waypoint_id bigint,
    player_id   varchar(60),
    shared_at   date not null,
    primary key (waypoint_id, player_id),
    foreign key (player_id) references players (player_id) on delete cascade,
    foreign key (waypoint_id) references waypoints (waypoint_id) on delete cascade
);

create table if not exists activated_waypoints
(
    waypoint_id bigint,
    player_id   varchar(60),
    primary key (waypoint_id, player_id),
    foreign key (waypoint_id) references waypoints (waypoint_id) on delete cascade,
    foreign key (player_id) references players (player_id) on delete cascade
);

-- Views

drop view if exists waypoint_view cascade;

create view waypoint_view as
select
    waypoint_id,
    waypoint_name,
    icon,
    coord_x,
    coord_y,
    coord_z,
    world,
    created_at,
    owner_id,
    p.player_name as owner_name
from waypoints as w
         join players as p on w.owner_id = p.player_id;

drop view if exists waypoint_share_view cascade;

create view waypoint_share_view as
select
    shared_at,
    p.player_id as shared_with_id,
    p.player_name as shared_with_name,
    wv.*
from shared_waypoints as sw
         join waypoint_view as wv on sw.waypoint_id = wv.waypoint_id
         join players as p on sw.player_id = p.player_id;

-- Triggers

drop trigger if exists trigger_remove_activated_waypoint_when_unshare on shared_waypoints;
drop function if exists remove_activated_waypoint_when_unshare;

create or replace function remove_activated_waypoint_when_unshare()
    returns trigger as
$$
begin
    -- @DELIMITER $
    delete
    from activated_waypoints
    where waypoint_id = old.waypoint_id
      and player_id = old.player_id;
end;
    -- @DELIMITER ;
$$ language plpgsql;

create or replace trigger trigger_remove_activated_waypoint_when_unshare
    after delete
    on shared_waypoints
    for each row
execute function remove_activated_waypoint_when_unshare();
