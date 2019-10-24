package com.guicedee.entityassist.injections.strings;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

public class StringBigDecimalIDMapping
		extends EntityAssistIDMapping<String, BigDecimal>
{

	@Override
	public BigDecimal toObject(String dbReturned)
	{
		return new BigDecimal(dbReturned);
	}
}
