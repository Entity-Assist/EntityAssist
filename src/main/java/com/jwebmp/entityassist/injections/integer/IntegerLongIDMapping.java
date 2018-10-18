package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class IntegerLongIDMapping
		extends EntityAssistIDMapping<Integer, Long>
{

	@Override
	public Long toObject(Integer dbReturned)
	{
		return dbReturned.longValue();
	}
}
