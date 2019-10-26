package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class IntegerBigIntegerIDMapping
		extends EntityAssistIDMapping<Integer, BigInteger>
{

	@Override
	public BigInteger toObject(Integer dbReturned)
	{
		return BigInteger.valueOf(dbReturned);
	}
}
