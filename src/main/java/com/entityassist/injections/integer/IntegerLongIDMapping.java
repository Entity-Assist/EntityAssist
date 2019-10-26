package com.entityassist.injections.integer;

import com.entityassist.services.EntityAssistIDMapping;

public class IntegerLongIDMapping
		extends EntityAssistIDMapping<Integer, Long>
{

	@Override
	public Long toObject(Integer dbReturned)
	{
		return dbReturned.longValue();
	}
}
