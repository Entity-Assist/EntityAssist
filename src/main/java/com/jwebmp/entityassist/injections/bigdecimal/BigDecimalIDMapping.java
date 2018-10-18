package com.jwebmp.entityassist.injections.bigdecimal;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

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
