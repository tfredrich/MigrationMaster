package com.strategicgains.cassandra.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Session;

public class ScriptMigration
extends AbstractMigration
{
	private static final Logger LOG = LoggerFactory.getLogger(ScriptMigration.class);

	private String script;

	public ScriptMigration()
	{
		super();
	}

	public String getScript()
	{
		return script;
	}

	public void setScript(String script)
	{
		this.script = script;
	}

	public boolean migrate(Session session)
	{
		try
		{
			String[] commands = getScript().split(";");

			for (String command : commands)
			{
				session.execute(command);
			}
		}
		catch(Exception t)
		{
			LOG.error("Migration failed: " + getDescription(), t);
			return false;
		}

		return true;
	}
}
