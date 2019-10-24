package com.guicedee.entityassist.entities.builders;

import com.google.inject.Key;
import com.guicedee.entityassist.querybuilder.QueryBuilderCore;
import com.guicedee.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.guicedee.entityassist.entities.EntityClassTwo;
import com.guicedee.guicedinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassTwoBuilder
		extends QueryBuilderCore<EntityClassTwoBuilder, EntityClassTwo, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}

	@Override
	protected boolean onCreate(EntityClassTwo entity)
	{
		return true;
	}

	@Override
	protected boolean isIdGenerated()
	{
		return false;
	}

	@Override
	protected boolean onUpdate(EntityClassTwo entity)
	{
		return true;
	}
}
