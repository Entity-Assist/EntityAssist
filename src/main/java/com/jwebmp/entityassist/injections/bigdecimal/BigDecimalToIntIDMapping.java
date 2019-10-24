package com.guicedee.entityassist.injections.bigdecimal;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToIntIDMapping
		extends EntityAssistIDMapping<BigDecimal, Integer>
{
	@Override
	public Integer toObject(BigDecimal dbReturned)
	{
		return dbReturned.intValue();
	}
}
