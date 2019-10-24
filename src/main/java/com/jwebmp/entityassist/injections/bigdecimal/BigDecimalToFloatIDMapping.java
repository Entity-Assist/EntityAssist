package com.guicedee.entityassist.injections.bigdecimal;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToFloatIDMapping
		extends EntityAssistIDMapping<BigDecimal, Float>
{
	@Override
	public Float toObject(BigDecimal dbReturned)
	{
		return dbReturned.floatValue();
	}
}
