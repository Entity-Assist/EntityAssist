package com.guicedee.entityassist.injections.integer;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class IntegerStringIDMapping
		extends EntityAssistIDMapping<Integer, String>
{

	@Override
	public String toObject(Integer dbReturned)
	{
		return dbReturned.toString();
	}
}
