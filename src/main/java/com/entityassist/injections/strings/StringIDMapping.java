package com.entityassist.injections.strings;

import com.entityassist.services.EntityAssistIDMapping;

public class StringIDMapping
		extends EntityAssistIDMapping<String, String>
{

	@Override
	public String toObject(String dbReturned)
	{
		return dbReturned;
	}
}
