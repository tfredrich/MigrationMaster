package com.pingidentity.db.migration.metadata;

import java.util.Date;

import com.pingidentity.db.migration.Migration;
import com.pingidentity.db.migration.ScriptMigration;

public class Metadata
{
	private int version;
	private String description;
	private String script;
	private String hash;
	private Date installedAt = new Date();
	private long executionTimeMillis;
	private boolean wasSuccessful;

	public Metadata(Migration migration, long executionTimeMillis, boolean wasSuccessful)
	{
		super();
		this.version = migration.getVersion();
		this.description = migration.getDescription();
		this.executionTimeMillis = executionTimeMillis;
		this.wasSuccessful = wasSuccessful;

		if (migration instanceof ScriptMigration)
		{
			setScript(((ScriptMigration) migration).getScript());
		}
	}

	private void setScript(String script)
	{
		this.script = script;
		MD5 md5 = MD5.ofString(script);
		this.hash = md5.asBase64();
	}

	public int getVersion()
	{
		return version;
	}

	public String getDescription()
	{
		return description;
	}

	public String getScript()
	{
		return script;
	}

	public String getHash()
	{
		return hash;
	}

	public Date getInstalledAt()
	{
		return installedAt;
	}

	public long getExecutionTime()
	{
		return executionTimeMillis;
	}

	public boolean isWasSuccessful()
	{
		return wasSuccessful;
	}
}
