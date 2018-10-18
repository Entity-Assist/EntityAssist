package com.jwebmp.entityassist.injections.biginteger;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerFloatIDMapping
		extends EntityAssistIDMapping<BigInteger, Float>
{

	@Override
	public Float toObject(BigInteger dbReturned)
	{
		return dbReturned.floatValue();
	}
}
