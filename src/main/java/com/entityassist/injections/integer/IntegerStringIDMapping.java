package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

public class IntegerStringIDMapping
		implements EntityAssistIDMapping<Integer, String>
{

	@Override
	public String toObject(Integer dbReturned)
	{
		return dbReturned.toString();
	}
}
