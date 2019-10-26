package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

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
