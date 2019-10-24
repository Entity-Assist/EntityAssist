package com.guicedee.entityassist.entities.builders;

import com.google.inject.Key;
import com.guicedee.entityassist.querybuilder.QueryBuilderCore;
import com.guicedee.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.guicedee.entityassist.entities.EntityClass;
import com.guicedee.guicedinjection.GuiceContext;

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
		return GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}

	@Override
	protected boolean onCreate(EntityClass entity)
	{
		return true;
	}

	@Override
	protected boolean isIdGenerated()
	{
		return false;
	}

	@Override
	protected boolean onUpdate(EntityClass entity)
	{
		return true;
	}
}
