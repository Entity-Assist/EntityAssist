package com.jwebmp.entityassist.injections.biginteger;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerStringIDMapping
		implements EntityAssistIDMapping<BigInteger, String>
{

	@Override
	public String toObject(BigInteger dbReturned)
	{
		return dbReturned.toString();
	}
}
