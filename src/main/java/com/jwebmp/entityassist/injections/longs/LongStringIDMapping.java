package com.guicedee.entityassist.injections.longs;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class LongStringIDMapping
		extends EntityAssistIDMapping<Long, String>
{

	@Override
	public String toObject(Long dbReturned)
	{
		return dbReturned.toString();
	}
}
