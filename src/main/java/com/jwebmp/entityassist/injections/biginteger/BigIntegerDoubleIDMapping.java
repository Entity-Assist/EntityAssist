package com.jwebmp.entityassist.injections.biginteger;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerDoubleIDMapping
		implements EntityAssistIDMapping<BigInteger, Double>
{

	@Override
	public Double toObject(BigInteger dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
