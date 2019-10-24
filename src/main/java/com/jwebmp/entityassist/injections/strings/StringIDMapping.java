package com.guicedee.entityassist.injections.strings;

import com.guicedee.entityassist.services.EntityAssistIDMapping;

public class StringIDMapping
		extends EntityAssistIDMapping<String, String>
{

	@Override
	public String toObject(String dbReturned)
	{
		return dbReturned;
	}
}
