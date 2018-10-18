package com.jwebmp.entityassist.injections.biginteger;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

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
