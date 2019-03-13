package com.jwebmp.entityassist.entities.builders;

import com.google.inject.Key;
import com.jwebmp.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.jwebmp.entityassist.entities.EntityClassTwo;
import com.jwebmp.entityassist.querybuilder.QueryBuilderCore;
import com.jwebmp.guicedinjection.GuiceContext;

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
