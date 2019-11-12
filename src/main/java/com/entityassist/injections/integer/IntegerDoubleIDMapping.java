package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

public class IntegerDoubleIDMapping
		implements EntityAssistIDMapping<Integer, Double>
{

	@Override
	public Double toObject(Integer dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
