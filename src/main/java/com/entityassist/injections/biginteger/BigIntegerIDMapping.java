package com.entityassist.injections.biginteger;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerIDMapping
		implements EntityAssistIDMapping<BigInteger, BigInteger>
{

	@Override
	public BigInteger toObject(BigInteger dbReturned)
	{
		return dbReturned;
	}
}
