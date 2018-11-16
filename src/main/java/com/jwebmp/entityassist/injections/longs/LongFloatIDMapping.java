package com.jwebmp.entityassist.injections.longs;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class LongFloatIDMapping
		extends EntityAssistIDMapping<Long, Float>
{

	@Override
	public Float toObject(Long dbReturned)
	{
		return dbReturned.floatValue();
	}
}
