package com.entityassist.injections.bigdecimal;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToDoubleIDMapping
		implements EntityAssistIDMapping<BigDecimal, Double>
{
	@Override
	public Double toObject(BigDecimal dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
