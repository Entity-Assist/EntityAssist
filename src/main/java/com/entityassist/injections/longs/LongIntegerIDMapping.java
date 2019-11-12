package com.entityassist.injections.longs;

import com.entityassist.services.EntityAssistIDMapping;

public class LongIntegerIDMapping
		implements EntityAssistIDMapping<Long, Integer>
{

	@Override
	public Integer toObject(Long dbReturned)
	{
		return dbReturned.intValue();
	}
}
