package com.jwebmp.entityassist.injections.longs;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class LongFloatIDMapping
		implements EntityAssistIDMapping<Long, Float>
{

	@Override
	public Float toObject(Long dbReturned)
	{
		return dbReturned.floatValue();
	}
}
