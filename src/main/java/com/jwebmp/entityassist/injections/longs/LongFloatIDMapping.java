package com.guicedee.entityassist.injections.longs;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class LongFloatIDMapping
		extends EntityAssistIDMapping<Long, Float>
{

	@Override
	public Float toObject(Long dbReturned)
	{
		return dbReturned.floatValue();
	}
}
