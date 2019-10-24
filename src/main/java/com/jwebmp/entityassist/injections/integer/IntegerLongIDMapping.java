package com.guicedee.entityassist.injections.integer;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class IntegerLongIDMapping
		extends EntityAssistIDMapping<Integer, Long>
{

	@Override
	public Long toObject(Integer dbReturned)
	{
		return dbReturned.longValue();
	}
}
