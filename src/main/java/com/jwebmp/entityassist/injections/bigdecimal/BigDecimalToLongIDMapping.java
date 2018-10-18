package com.jwebmp.entityassist.injections.bigdecimal;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToLongIDMapping
		extends EntityAssistIDMapping<BigDecimal, Long>
{
	@Override
	public Long toObject(BigDecimal dbReturned)
	{
		return dbReturned.longValue();
	}
}
