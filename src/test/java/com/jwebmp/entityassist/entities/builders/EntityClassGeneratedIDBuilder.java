package com.jwebmp.entityassist.entities.builders;

import com.google.inject.Key;
import com.jwebmp.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.jwebmp.entityassist.entities.EntityClassGeneratedID;
import com.jwebmp.entityassist.querybuilder.QueryBuilderCore;
import com.jwebmp.guicedinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassGeneratedIDBuilder
		extends QueryBuilderCore<EntityClassGeneratedIDBuilder, EntityClassGeneratedID, Long>
{
	public EntityClassGeneratedIDBuilder()
	{
	}

	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}

}
