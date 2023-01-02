package com.entityassist;

import com.entityassist.querybuilder.*;

public class EntityClassTwoBuilder
		extends QueryBuilderCore<EntityClassTwoBuilder, EntityClassTwo, Long>
{

	@Override
	public boolean onCreate(EntityClassTwo entity)
	{
		return true;
	}

	@Override
	public boolean onUpdate(EntityClassTwo entity)
	{
		return true;
	}
}
