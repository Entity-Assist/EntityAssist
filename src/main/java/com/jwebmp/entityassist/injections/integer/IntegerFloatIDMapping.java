package com.guicedee.entityassist.injections.integer;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class IntegerFloatIDMapping
		extends EntityAssistIDMapping<Integer, Float>
{

	@Override
	public Float toObject(Integer dbReturned)
	{
		return dbReturned.floatValue();
	}
}
