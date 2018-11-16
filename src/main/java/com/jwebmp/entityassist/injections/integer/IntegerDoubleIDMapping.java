package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class IntegerDoubleIDMapping
		extends EntityAssistIDMapping<Integer, Double>
{

	@Override
	public Double toObject(Integer dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
