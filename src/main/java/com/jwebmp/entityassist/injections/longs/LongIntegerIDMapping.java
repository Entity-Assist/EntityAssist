package com.guicedee.entityassist.injections.longs;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class LongIntegerIDMapping
		extends EntityAssistIDMapping<Long, Integer>
{

	@Override
	public Integer toObject(Long dbReturned)
	{
		return dbReturned.intValue();
	}
}
