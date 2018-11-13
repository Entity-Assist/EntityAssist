package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class IntegerLongIDMapping
		implements EntityAssistIDMapping<Integer, Long>
{

	@Override
	public Long toObject(Integer dbReturned)
	{
		return dbReturned.longValue();
	}
}
