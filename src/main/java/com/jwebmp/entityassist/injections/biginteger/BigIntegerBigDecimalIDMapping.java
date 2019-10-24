package com.guicedee.entityassist.injections.biginteger;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerBigDecimalIDMapping
		extends EntityAssistIDMapping<BigInteger, BigDecimal>
{

	@Override
	public BigDecimal toObject(BigInteger dbReturned)
	{
		return new BigDecimal(dbReturned);
	}
}
