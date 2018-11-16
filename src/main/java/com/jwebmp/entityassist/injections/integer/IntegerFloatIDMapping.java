package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class IntegerFloatIDMapping
		extends EntityAssistIDMapping<Integer, Float>
{

	@Override
	public Float toObject(Integer dbReturned)
	{
		return dbReturned.floatValue();
	}
}
