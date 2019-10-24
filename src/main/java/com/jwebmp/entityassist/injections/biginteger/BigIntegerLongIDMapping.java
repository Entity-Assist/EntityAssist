package com.guicedee.entityassist.injections.biginteger;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerLongIDMapping
		extends EntityAssistIDMapping<BigInteger, Long>
{

	@Override
	public Long toObject(BigInteger dbReturned)
	{
		return dbReturned.longValue();
	}
}
