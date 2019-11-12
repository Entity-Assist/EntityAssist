package com.entityassist.injections.strings;

import com.entityassist.services.EntityAssistIDMapping;

public class StringLongIDMapping
		implements EntityAssistIDMapping<String, Long>
{

	@Override
	public Long toObject(String dbReturned)
	{
		return Long.valueOf(dbReturned);
	}
}
