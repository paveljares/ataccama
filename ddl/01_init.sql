/*
 * database viewer
 *  - to store connection strings to another databases
 */ 

drop database if exists viewer;

create database viewer;

connect viewer;

create sequence HIBERNATE_SEQUENCE INCREMENT by 1 START with 1;

create table sys_connection (
	`id` bigint not null,
	`version` smallint not null default 0,
	`name` varchar(50) not null,
	`hostname` varchar(50) not null,
	`port` int not null default 3306,
	`database_name` varchar(50) not null,
	`username` varchar(50) not null,
	`password` varchar(50) not null,
	CONSTRAINT connection_pk primary key (`id`)
);

/**
 * Test database 1
 */
drop database if exists test1;

create database test1;

connect test1;

create table table_a (
	`id` bigint,
	`name` varchar(20),
	`description` varchar(30),
	constraint table_a_pk primary key (`id`)
);

insert into table_a (`id`, `name`, `description`)
	value (1, 'name 1', 'description 1');
insert into table_a (`id`, `name`, `description`)
	value (2, 'name 2', 'description 2');
insert into table_a (`id`, `name`, `description`)
	value (3, 'name 3', 'description 3');
insert into table_a (`id`, `name`, `description`)
	value (4, 'name 4', 'description 4');
insert into table_a (`id`, `name`, `description`)
	value (5, 'name 5', 'description 5');
insert into table_a (`id`, `name`, `description`)
	value (6, 'name 6', 'description 6');
insert into table_a (`id`, `name`, `description`)
	value (7, 'name 7', 'description 7');
insert into table_a (`id`, `name`, `description`)
	value (8, 'name 8', 'description 8');
insert into table_a (`id`, `name`, `description`)
	value (9, 'name 9', 'description 9');
insert into table_a (`id`, `name`, `description`)
	value (10, 'name 10', 'description 10');
insert into table_a (`id`, `name`, `description`)
	value (11, 'name 11', 'description 11');
insert into table_a (`id`, `name`, `description`)
	value (12, 'name 12', 'description 12');
insert into table_a (`id`, `name`, `description`)
	value (13, 'name 13', 'description 13');
insert into table_a (`id`, `name`, `description`)
	value (14, 'name 14', 'description 14');
insert into table_a (`id`, `name`, `description`)
	value (15, 'name 15', 'description 15');
insert into table_a (`id`, `name`, `description`)
	value (16, 'name 16', 'description 16');
insert into table_a (`id`, `name`, `description`)
	value (17, 'name 17', 'description 17');
insert into table_a (`id`, `name`, `description`)
	value (18, 'name 18', 'description 18');
insert into table_a (`id`, `name`, `description`)
	value (19, 'name 19', 'description 19');
insert into table_a (`id`, `name`, `description`)
	value (20, 'name 20', 'description 20');

create table table_b (
	`id` bigint,
	`value` bigint,
	constraint table_b_pk primary key (`id`)
);

insert into table_b (`id`, `value`) select id, id+100 from table_a;
insert into table_b (`id`, `value`) select id+20, id+100 from table_a;

commit;

/*
 * users
 */
 
drop user if exists user;
create user 'user' identified by 'password';
GRANT ALL privileges ON `viewer`.* TO 'user';
GRANT ALL privileges ON `test1`.* TO 'user';

commit;
