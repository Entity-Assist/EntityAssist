package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

public class IntegerFloatIDMapping
		implements EntityAssistIDMapping<Integer, Float>
{

	@Override
	public Float toObject(Integer dbReturned)
	{
		return dbReturned.floatValue();
	}
}
