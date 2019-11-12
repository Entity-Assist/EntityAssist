package com.entityassist.injections.longs;

import com.entityassist.services.EntityAssistIDMapping;

public class LongFloatIDMapping
		implements EntityAssistIDMapping<Long, Float>
{

	@Override
	public Float toObject(Long dbReturned)
	{
		return dbReturned.floatValue();
	}
}
