create table if not exists post (
	id serial primary key,
	name varchar(255),
	link varchar(255) unique,
	text text,
	created timestamp
);