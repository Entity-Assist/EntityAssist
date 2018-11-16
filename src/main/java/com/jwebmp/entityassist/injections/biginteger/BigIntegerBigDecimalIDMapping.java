package com.jwebmp.entityassist.injections.biginteger;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

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
