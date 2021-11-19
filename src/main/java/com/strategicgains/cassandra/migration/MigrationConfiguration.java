package com.strategicgains.cassandra.migration;

import java.util.Properties;

public class MigrationConfiguration
{
	public static final String DEFAULT_SCRIPT_LOCATION = "/db/migrations";
	private static final String DEFAULT_METADATA_TABLE = "migration_meta";
	private static final String DEFAULT_METADATA_KEYSPACE = "migrations";

	private static final String SCRIPT_LOCATION = "migration.script_location";
	private static final String METADATA_TABLE = "migration.metadata_table";
	private static final String METADATA_KEYSPACE = "migration.metadata_keyspace";

	private String scriptLocation = DEFAULT_SCRIPT_LOCATION;
	private String metadataTable = DEFAULT_METADATA_TABLE;
	private String keyspace = DEFAULT_METADATA_KEYSPACE;

	public MigrationConfiguration()
	{
		super();
	}

	/**
	 * Allow setup of MigrationConfiguration from a Properties file.
	 * 
	 * Supported properties file keys are:
	 * migration.script_location (default is to use the classpath + /db/migrations)
	 * migration.metadata_table (default is migration_meta)
	 * migration.metadata_keyspace (default is migrations)
	 * 
	 * @param properties
	 */
	public MigrationConfiguration(Properties properties)
	{
		setScriptLocation(properties.getProperty(SCRIPT_LOCATION, DEFAULT_SCRIPT_LOCATION));
		setMetadataTable(properties.getProperty(METADATA_TABLE, DEFAULT_METADATA_TABLE));
		setKeyspace(properties.getProperty(METADATA_KEYSPACE, DEFAULT_METADATA_KEYSPACE));
	}

	/**
	 * Set the name of the Cassandra table that migration metadata will be stored for
	 * each migration performed. This table will be initialized on execution of the
	 * first migration performed. Default is migration_meta.
	 * 
	 * @param tableName
	 * @return
	 */
	public MigrationConfiguration setMetadataTable(String tableName)
	{
		this.metadataTable = tableName;
		return this;
	}

	public String getMetadataTable()
	{
		return metadataTable;
	}

	public String getKeyspace()
	{
		return keyspace;
	}

	/**
	 * Set the Cassandra keyspace name in which to place the metadata table.
	 * This keyspace must already exist. Default is migrations.
	 * 
	 * @param keyspaceName
	 * @return
	 */
	public MigrationConfiguration setKeyspace(String keyspaceName)
	{
		this.keyspace = keyspaceName;
		return this;
	}

	public String getScriptLocation()
	{
		return scriptLocation;
	}

	/**
	 * Set the path (within the classpath) to where Cassandra (CQL) migration scripts
	 * are located. Default is /db/migrations.
	 * 
	 * @param scriptLocation
	 * @return
	 */
	public MigrationConfiguration setScriptLocation(String scriptLocation)
	{
		this.scriptLocation = scriptLocation;
		return this;
	}
}
