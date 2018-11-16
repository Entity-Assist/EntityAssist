package com.jwebmp.entityassist.injections.strings;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class StringIntegerIDMapping
		extends EntityAssistIDMapping<String, Integer>
{

	@Override
	public Integer toObject(String dbReturned)
	{
		return Integer.valueOf(dbReturned);
	}
}
