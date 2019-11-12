package com.entityassist.injections.biginteger;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerLongIDMapping
		implements EntityAssistIDMapping<BigInteger, Long>
{

	@Override
	public Long toObject(BigInteger dbReturned)
	{
		return dbReturned.longValue();
	}
}
