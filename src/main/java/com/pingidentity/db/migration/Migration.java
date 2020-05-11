package com.pingidentity.db.migration;

import com.datastax.driver.core.Session;

public interface Migration
extends Comparable<Migration>
{
	public int getVersion();
	public String getDescription();
	public boolean isApplicable(int from, int to);
	public boolean migrate(Session session)
	throws MigrationException;
}
