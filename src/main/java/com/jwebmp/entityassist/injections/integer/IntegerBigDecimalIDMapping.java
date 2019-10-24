package com.guicedee.entityassist.injections.integer;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

public class IntegerBigDecimalIDMapping
		extends EntityAssistIDMapping<Integer, BigDecimal>
{

	@Override
	public BigDecimal toObject(Integer dbReturned)
	{
		return new BigDecimal(dbReturned);
	}
}
