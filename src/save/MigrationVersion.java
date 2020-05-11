package com.strategicgains.cassandra.migration;

import java.util.regex.Pattern;

public class MigrationVersion
implements Comparable<MigrationVersion>
{
	private static final Pattern VERSION_PATTERN = Pattern.compile("\\.(?=\\d)");
	public static final MigrationVersion LATEST = new MigrationVersion("** Latest **", Integer.MAX_VALUE);
	public static final MigrationVersion CURRENT = new MigrationVersion("** Current **", Integer.MIN_VALUE);

	private String text;
	private int[] parts;

	public MigrationVersion(String text)
	{
		this.text = text;
		this.parts = parse(text);
	}

	private MigrationVersion(String text, int version)
	{
		this.text = text;
		parts = new int[1];
		parts[0] = version;
	}

	public static MigrationVersion from(String version)
	{
		if ("current".equalsIgnoreCase(version.trim())) return CURRENT;
		if ("latest".equalsIgnoreCase(version.trim())) return LATEST;
		return new MigrationVersion(version);
	}

	public boolean isCurrent()
	{
		return (this == CURRENT);
	}

	public boolean isLatest()
	{
		return (this == LATEST);
	}

	public int size()
	{
		return (parts != null ? parts.length : 0);
	}

	public String getText()
	{
		return text;
	}

	@Override
	public String toString()
	{
		return getText();
	}

	@Override
	public boolean equals(Object that)
	{
		if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

		return compareTo((MigrationVersion) that) == 0;
	}

	public int compareTo(MigrationVersion that)
	{
		if (that == null) return 1;

		if (isCurrent())
		{
			return (that.isCurrent() ? 0 : -1);
		}

		if (isLatest())
		{
			return (that.isLatest() ? 0 : 1);
		}

		if (that.isCurrent()) return 1;
		if (that.isLatest()) return -1;

		int length = Math.min(size(), that.size());

		for (int i = 0; i < length; i++)
		{
			int diff = this.parts[i] - that.parts[i];

			if (diff != 0) return diff;
		}

		if (length > this.parts.length)
		{
			if (hasNonZeroParts(that.parts, this.parts.length)) return -1;
		}
		else if (length > that.parts.length)
		{
			if (hasNonZeroParts(this.parts, that.parts.length)) return 1;
		}

		return 0;
	}

	private boolean hasNonZeroParts(int[] parts, int offset)
	{
		for (int i = offset; i < parts.length; i++)
		{
			if (parts[i] != 0) return true;
		}

		return false;
	}

	private int[] parse(String string)
	{
		String[] tokens = VERSION_PATTERN.split(string);
		int[] ints = new int[tokens.length];

		for(int i = 0; i < tokens.length; i++)
		{
			try
			{
				ints[i] = Integer.valueOf(tokens[i]);
			}
			catch(NumberFormatException e)
			{
				throw new VersionException("Invalid version. Only 0-9 and '.' allowed. Version: " + string, e);
			}
		}

		return ints;
	}
}
