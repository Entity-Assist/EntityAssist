package com.guicedee.entityassist.injections.integer;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class IntegerDoubleIDMapping
		extends EntityAssistIDMapping<Integer, Double>
{

	@Override
	public Double toObject(Integer dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
