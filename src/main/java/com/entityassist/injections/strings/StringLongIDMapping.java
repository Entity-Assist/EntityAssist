package com.entityassist.injections.strings;

import com.entityassist.services.EntityAssistIDMapping;

public class StringLongIDMapping
		extends EntityAssistIDMapping<String, Long>
{

	@Override
	public Long toObject(String dbReturned)
	{
		return Long.valueOf(dbReturned);
	}
}
