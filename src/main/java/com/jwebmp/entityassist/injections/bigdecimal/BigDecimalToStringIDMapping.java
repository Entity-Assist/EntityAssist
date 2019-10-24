package com.guicedee.entityassist.injections.bigdecimal;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToStringIDMapping
		extends EntityAssistIDMapping<BigDecimal, String>
{
	@Override
	public String toObject(BigDecimal dbReturned)
	{
		return dbReturned.toPlainString();
	}
}
