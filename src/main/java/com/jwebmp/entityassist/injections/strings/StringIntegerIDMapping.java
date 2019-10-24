package com.guicedee.entityassist.injections.strings;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class StringIntegerIDMapping
		extends EntityAssistIDMapping<String, Integer>
{

	@Override
	public Integer toObject(String dbReturned)
	{
		return Integer.valueOf(dbReturned);
	}
}
