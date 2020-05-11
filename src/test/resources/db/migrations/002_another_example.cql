use example;

create table if not exists table1 (
	name text,
	version int,
	description text,
	script text,
	hash text,
	installed_at timestamp,
	execution_time int,
	was_successful boolean,
	primary key ((name), installed_at, version)
)
with clustering order by (installed_at DESC, version DESC);