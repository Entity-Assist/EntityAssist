package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class IntegerDoubleIDMapping
		implements EntityAssistIDMapping<Integer, Double>
{

	@Override
	public Double toObject(Integer dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
