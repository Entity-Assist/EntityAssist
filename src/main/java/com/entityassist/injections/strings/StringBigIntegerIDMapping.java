package com.entityassist.injections.strings;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class StringBigIntegerIDMapping
		implements EntityAssistIDMapping<String, BigInteger>
{

	@Override
	public BigInteger toObject(String dbReturned)
	{
		return new BigInteger(dbReturned);
	}
}
