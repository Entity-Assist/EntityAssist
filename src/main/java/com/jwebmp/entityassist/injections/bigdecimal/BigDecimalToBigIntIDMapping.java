package com.jwebmp.entityassist.injections.bigdecimal;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToBigIntIDMapping
		implements EntityAssistIDMapping<BigDecimal, BigInteger>
{
	@Override
	public BigInteger toObject(BigDecimal dbReturned)
	{
		return dbReturned.toBigInteger();
	}
}
