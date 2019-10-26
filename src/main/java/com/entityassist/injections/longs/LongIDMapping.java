package com.entityassist.injections.longs;

import com.entityassist.services.EntityAssistIDMapping;

public class LongIDMapping
		extends EntityAssistIDMapping<Long, Long>
{

	@Override
	public Long toObject(Long dbReturned)
	{
		return dbReturned;
	}
}
