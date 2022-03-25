package com.strategicgains.cassandra.migration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Session;
import com.strategicgains.cassandra.migration.metadata.CassandraMetadataStrategy;
import com.strategicgains.cassandra.migration.metadata.Metadata;
import com.strategicgains.cassandra.migration.metadata.MetadataStrategy;

/**
 * @author tfredrich
 *
 */
public class MigrationMaster
{
	private static final Logger LOG = LoggerFactory.getLogger(MigrationMaster.class);

	private static final int UNINITIALIZED = -1;

	private MigrationConfiguration configuration;
	private Set<Migration> migrations = new HashSet<>();
	private MetadataStrategy metadata;

	/**
	 * Manage migrations using the default configuration.
	 */
	public MigrationMaster()
	{
		this(new MigrationConfiguration());
	}

	/**
	 * Manage migrations using the provided configuration.
	 * 
	 * @param configuration
	 */
	public MigrationMaster(MigrationConfiguration configuration)
	{
		super();
		setConfiguration(configuration);
	}

	public void setConfiguration(MigrationConfiguration configuration)
	{
		this.configuration = configuration;
		this.metadata = new CassandraMetadataStrategy(configuration);
	}

	public MigrationConfiguration getConfiguration()
	{
		return configuration;
	}

	public MigrationMaster register(Migration migration)
	{
		migrations.add(migration);
		return this;
	}

	public MigrationMaster registerAll(Collection<Migration> migrations)
	{
		this.migrations.addAll(migrations);
		return this;
	}

	public void migrate(final Session session)
	throws IOException
	{
		if (!acquireLock(session))
		{
			LOG.info("Data migration in process. Waiting...");
			hold(session);
			LOG.info("Data migration complete. Continuing...");
			return;
		}

		int currentVersion = getCurrentVersion(session);

		if (currentVersion == UNINITIALIZED)
		{
			currentVersion = initializeMetadata(session);
		}

		Collection<Migration> scriptMigrations = discoverMigrationScripts();
		List<Migration> allMigrations = new ArrayList<>(migrations.size() + scriptMigrations.size());
		allMigrations.addAll(migrations);
		allMigrations.addAll(scriptMigrations);
		Collections.sort(allMigrations, Collections.reverseOrder());
		int latestVersion = allMigrations.get(allMigrations.size() -1).getVersion();

		if (currentVersion < latestVersion)
		{
			LOG.info("Database needs migration from current version {} to version {}", currentVersion, latestVersion);

			try
			{
				boolean wasSuccessful = process(session, allMigrations, currentVersion, latestVersion);
	
				if (!wasSuccessful)
				{
					throw new MigrationException("Migration aborted");
				}
	
				LOG.info("Database migration completed successfuly.");
			}
			finally
			{
				releaseLock(session);
			}
		}
	}

	private void hold(Session session)
	{
		do
		{
			try
			{
				Thread.sleep(1000l);
			}
			catch (InterruptedException e)
			{
				LOG.warn("Database migration interrupted", e);
				// Restore interrupted state...
			    Thread.currentThread().interrupt();
				return;
			}
		}
		while (metadata.isLocked(session));
	}

	private boolean acquireLock(Session session)
	{
		return metadata.acquireLock(session);
	}

	private void releaseLock(Session session)
	{
		metadata.releaseLock(session);
	}

	private boolean process(final Session session, final List<Migration> allMigrations, final int from, final int to)
	{
		for (Migration migration : allMigrations)
		{
			if (migration.isApplicable(from, to))
			{
				LOG.info("Migrating database to version {}.", migration.getVersion());
				long startTimeMillis = System.currentTimeMillis();
				boolean wasSuccessful = migration.migrate(session);
				long executionTime = System.currentTimeMillis() - startTimeMillis;
				metadata.update(session, new Metadata(migration, executionTime, wasSuccessful));

				if (!wasSuccessful)
				{
					return false;
				}
			}
		}

		return true;
	}

	private Collection<Migration> discoverMigrationScripts()
	throws IOException
	{
		return new ClasspathMigrationLoader().load(configuration);
	}

	private int getCurrentVersion(Session session)
	{
		if (!metadata.exists(session))
		{
			return UNINITIALIZED;
		}

		return metadata.getCurrentVersion(session);
	}

	private int initializeMetadata(Session session)
	{
		metadata.initialize(session);
		return 0;
	}
}
