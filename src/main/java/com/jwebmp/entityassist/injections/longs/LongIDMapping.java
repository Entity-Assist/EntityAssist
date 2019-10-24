package com.guicedee.entityassist.injections.longs;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class LongIDMapping
		extends EntityAssistIDMapping<Long, Long>
{

	@Override
	public Long toObject(Long dbReturned)
	{
		return dbReturned;
	}
}
