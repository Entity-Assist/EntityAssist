package com.entityassist.injections.longs;

import com.entityassist.services.EntityAssistIDMapping;

public class LongFloatIDMapping
		extends EntityAssistIDMapping<Long, Float>
{

	@Override
	public Float toObject(Long dbReturned)
	{
		return dbReturned.floatValue();
	}
}
