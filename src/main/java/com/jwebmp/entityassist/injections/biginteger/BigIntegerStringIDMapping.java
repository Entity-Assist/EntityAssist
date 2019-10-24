package com.guicedee.entityassist.injections.biginteger;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerStringIDMapping
		extends EntityAssistIDMapping<BigInteger, String>
{

	@Override
	public String toObject(BigInteger dbReturned)
	{
		return dbReturned.toString();
	}
}
