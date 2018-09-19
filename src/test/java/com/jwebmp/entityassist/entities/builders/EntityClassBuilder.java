package com.jwebmp.entityassist.entities.builders;

import com.google.inject.Key;
import com.jwebmp.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.jwebmp.entityassist.entities.EntityClass;
import com.jwebmp.entityassist.querybuilder.QueryBuilderCore;
import com.jwebmp.guicedinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassBuilder
		extends QueryBuilderCore<EntityClassBuilder, EntityClass, Long>
{
	public EntityClassBuilder()
	{
	}

	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}

	@Override
	protected boolean onCreate(EntityClass entity)
	{
		return true;
	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}

	@Override
	protected boolean onUpdate(EntityClass entity)
	{
		return true;
	}
}
