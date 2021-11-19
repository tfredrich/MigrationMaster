package com.strategicgains.cassandra.migration;

public abstract class AbstractMigration
implements Migration
{
	private int version;
	private String description;

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public boolean isApplicable(int from, int to)
	{
		return (getVersion() > from && getVersion() <= to);
	}

	public int compareTo(Migration that)
	{
		if (that == null) return 1;
		return that.getVersion() - this.getVersion();
	}
}
