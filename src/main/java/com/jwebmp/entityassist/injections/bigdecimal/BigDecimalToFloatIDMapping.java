package com.jwebmp.entityassist.injections.bigdecimal;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToFloatIDMapping
		implements EntityAssistIDMapping<BigDecimal, Float>
{
	@Override
	public Float toObject(BigDecimal dbReturned)
	{
		return dbReturned.floatValue();
	}
}
