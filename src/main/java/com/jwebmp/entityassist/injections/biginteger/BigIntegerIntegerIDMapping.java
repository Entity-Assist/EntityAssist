package com.guicedee.entityassist.injections.biginteger;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerIntegerIDMapping
		extends EntityAssistIDMapping<BigInteger, Integer>
{

	@Override
	public Integer toObject(BigInteger dbReturned)
	{
		return dbReturned.intValue();
	}
}
