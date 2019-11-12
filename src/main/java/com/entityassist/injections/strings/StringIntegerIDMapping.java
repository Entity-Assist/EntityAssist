package com.entityassist.injections.strings;

import com.entityassist.services.EntityAssistIDMapping;

public class StringIntegerIDMapping
		implements EntityAssistIDMapping<String, Integer>
{

	@Override
	public Integer toObject(String dbReturned)
	{
		return Integer.valueOf(dbReturned);
	}
}
