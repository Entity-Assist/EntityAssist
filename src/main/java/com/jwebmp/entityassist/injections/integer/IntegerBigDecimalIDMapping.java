package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

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
