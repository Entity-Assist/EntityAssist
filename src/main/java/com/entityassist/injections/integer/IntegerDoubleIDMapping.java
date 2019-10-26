package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

public class IntegerDoubleIDMapping
		extends EntityAssistIDMapping<Integer, Double>
{

	@Override
	public Double toObject(Integer dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
