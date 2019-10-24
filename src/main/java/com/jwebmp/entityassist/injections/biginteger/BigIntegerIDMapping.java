package com.guicedee.entityassist.injections.biginteger;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerIDMapping
		extends EntityAssistIDMapping<BigInteger, BigInteger>
{

	@Override
	public BigInteger toObject(BigInteger dbReturned)
	{
		return dbReturned;
	}
}
