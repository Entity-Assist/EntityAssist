package com.guicedee.entityassist.injections.bigdecimal;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToBigIntIDMapping
		extends EntityAssistIDMapping<BigDecimal, BigInteger>
{
	@Override
	public BigInteger toObject(BigDecimal dbReturned)
	{
		return dbReturned.toBigInteger();
	}
}
