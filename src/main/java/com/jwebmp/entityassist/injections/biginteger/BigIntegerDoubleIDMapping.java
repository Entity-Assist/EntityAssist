package com.guicedee.entityassist.injections.biginteger;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerDoubleIDMapping
		extends EntityAssistIDMapping<BigInteger, Double>
{

	@Override
	public Double toObject(BigInteger dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
