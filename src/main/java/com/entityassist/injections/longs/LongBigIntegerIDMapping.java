package com.entityassist.injections.longs;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class LongBigIntegerIDMapping
		implements EntityAssistIDMapping<Long, BigInteger>
{

	@Override
	public BigInteger toObject(Long dbReturned)
	{
		return BigInteger.valueOf(dbReturned);
	}
}
