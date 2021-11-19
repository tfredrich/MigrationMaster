package com.strategicgains.cassandra.migration.metadata;

import com.datastax.driver.core.Session;

public interface MetadataStrategy
{
	public boolean exists(Session session);
	public int getCurrentVersion(Session session);
	public void initialize(Session session);
	public void update(Session session, Metadata metadata);
	public boolean acquire(Session session);
	public boolean isLocked(Session session);
	public void release(Session session);
}
