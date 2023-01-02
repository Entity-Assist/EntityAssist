package com.entityassist;

import com.entityassist.querybuilder.*;

public class EntityClassBuilder
		extends QueryBuilderCore<EntityClassBuilder, EntityClass, Long>
{
	public EntityClassBuilder()
	{
	}
	
	@Override
	public boolean onCreate(EntityClass entity)
	{
		return true;
	}

	@Override
	public boolean onUpdate(EntityClass entity)
	{
		return true;
	}
}
