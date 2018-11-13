package com.jwebmp.entityassist.injections.bigdecimal;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToIntIDMapping
		implements EntityAssistIDMapping<BigDecimal, Integer>
{
	@Override
	public Integer toObject(BigDecimal dbReturned)
	{
		return dbReturned.intValue();
	}
}
