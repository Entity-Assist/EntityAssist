package com.jwebmp.entityassist.injections.longs;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class LongIDMapping
		extends EntityAssistIDMapping<Long, Long>
{

	@Override
	public Long toObject(Long dbReturned)
	{
		return dbReturned;
	}
}
