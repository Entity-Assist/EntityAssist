package com.jwebmp.entityassist.injections.longs;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class LongIntegerIDMapping
		extends EntityAssistIDMapping<Long, Integer>
{

	@Override
	public Integer toObject(Long dbReturned)
	{
		return dbReturned.intValue();
	}
}
