package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class IntegerStringIDMapping
		extends EntityAssistIDMapping<Integer, String>
{

	@Override
	public String toObject(Integer dbReturned)
	{
		return dbReturned.toString();
	}
}
