package com.jwebmp.entityassist.injections.strings;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

public class StringBigDecimalIDMapping
		implements EntityAssistIDMapping<String, BigDecimal>
{

	@Override
	public BigDecimal toObject(String dbReturned)
	{
		return new BigDecimal(dbReturned);
	}
}
