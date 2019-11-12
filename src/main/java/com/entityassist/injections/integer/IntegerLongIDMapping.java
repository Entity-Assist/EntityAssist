package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

public class IntegerLongIDMapping
		implements EntityAssistIDMapping<Integer, Long>
{

	@Override
	public Long toObject(Integer dbReturned)
	{
		return dbReturned.longValue();
	}
}
