package com.jwebmp.entityassist.injections.longs;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class LongBigIntegerIDMapping
		extends EntityAssistIDMapping<Long, BigInteger>
{

	@Override
	public BigInteger toObject(Long dbReturned)
	{
		return BigInteger.valueOf(dbReturned);
	}
}
