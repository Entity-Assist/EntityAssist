package com.guicedee.entityassist.entities.builders;

import com.google.inject.Key;
import com.guicedee.entityassist.querybuilder.QueryBuilderCore;
import com.guicedee.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.guicedee.entityassist.entities.EntityClassGeneratedID;
import com.guicedee.guicedinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassGeneratedIDBuilder
		extends QueryBuilderCore<EntityClassGeneratedIDBuilder, EntityClassGeneratedID, Long>
{
	public EntityClassGeneratedIDBuilder()
	{
		setSelectIdentityString("CALL SCOPE_IDENTITY();");
	}

	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}

}
