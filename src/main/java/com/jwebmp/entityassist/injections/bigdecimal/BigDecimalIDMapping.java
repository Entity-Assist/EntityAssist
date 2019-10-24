package com.guicedee.entityassist.injections.bigdecimal;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalIDMapping
		extends EntityAssistIDMapping<BigDecimal, BigDecimal>
{
	@Override
	public BigDecimal toObject(BigDecimal dbReturned)
	{
		return dbReturned;
	}
}
