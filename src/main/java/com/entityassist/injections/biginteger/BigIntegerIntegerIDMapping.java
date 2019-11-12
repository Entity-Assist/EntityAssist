package com.entityassist.injections.biginteger;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerIntegerIDMapping
		implements EntityAssistIDMapping<BigInteger, Integer>
{

	@Override
	public Integer toObject(BigInteger dbReturned)
	{
		return dbReturned.intValue();
	}
}
