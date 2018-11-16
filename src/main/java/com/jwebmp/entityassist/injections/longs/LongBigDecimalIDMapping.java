package com.jwebmp.entityassist.injections.longs;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

public class LongBigDecimalIDMapping
		extends EntityAssistIDMapping<Long, BigDecimal>
{

	@Override
	public BigDecimal toObject(Long dbReturned)
	{
		return new BigDecimal(dbReturned);
	}
}
