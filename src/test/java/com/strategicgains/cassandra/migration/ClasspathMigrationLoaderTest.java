package com.strategicgains.cassandra.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.strategicgains.cassandra.migration.ClasspathMigrationLoader;
import com.strategicgains.cassandra.migration.Migration;
import com.strategicgains.cassandra.migration.MigrationConfiguration;
import com.strategicgains.cassandra.migration.ScriptMigration;

public class ClasspathMigrationLoaderTest
{
	@Test
	public void shouldLoadScripts()
	throws IOException
	{
		Collection<Migration> migrations = new ClasspathMigrationLoader().load(new MigrationConfiguration());
		assertNotNull(migrations);
		assertEquals(2, migrations.size());

		ScriptMigration migration = (ScriptMigration) migrations.toArray()[0];
		assertEquals(1, migration.getVersion());
		assertEquals("001_example_script_to_load.cql", migration.getDescription());
		assertTrue(migration.getScript().startsWith("create keyspace if not exists example"));

		migration = (ScriptMigration) migrations.toArray()[1];
		assertEquals(2, migration.getVersion());
		assertEquals("002_another_example.cql", migration.getDescription());
		assertTrue(migration.getScript().startsWith("use example;"));
	}
}
