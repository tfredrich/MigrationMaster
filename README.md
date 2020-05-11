Cassandra Schema Evolution for Java
===================================

Usage:
```
	MigrationMaster migrations = new MigrationMaster();
	migrations.migrate(session);
```

By default, migration scripts (CQL files) exist in the /src/main/resources/db/migrations directory with a '.cql' suffix. The filenames must be in the form 001_description.cql (single underscore separates the integer version from the description) so that the version and description exist in the filename. For example:

001_initial.cql
```
create table if not exists example.table (
	name text,
	version int,
	description text,
	script text,
	hash text,
	installed_at timestamp,
	execution_time bigint,
	was_successful boolean,
	primary key ((name), installed_at, version)
)
with clustering order by (installed_at DESC, version DESC);
```
