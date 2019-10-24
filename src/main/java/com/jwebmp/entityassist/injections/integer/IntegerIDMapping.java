package com.guicedee.entityassist.injections.integer;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class IntegerIDMapping
		extends EntityAssistIDMapping<Integer, Integer>
{

	@Override
	public Integer toObject(Integer dbReturned)
	{
		return dbReturned.intValue();
	}
}
