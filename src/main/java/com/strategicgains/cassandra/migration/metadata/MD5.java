package com.strategicgains.cassandra.migration.metadata;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class MD5
{
	private final byte[] digest;

	public MD5(byte[] md5)
	{
		this.digest = md5;
	}

	/**
	 * Factory method converts String into MD5 object.
	 */
	public static MD5 ofString(String str)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return new MD5(md.digest());
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(this.digest);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null)
		{
			return false;
		}

		if (!(o instanceof MD5))
		{
			return false;
		}

		MD5 that = (MD5) o;
		return Arrays.equals(this.digest, that.digest);
	}

	/**
	 * returns a Base64 encoded version of the MD5 digest. 
	 */
	public String asBase64()
	{
		return Base64.getEncoder().encodeToString(this.digest);
	}
}