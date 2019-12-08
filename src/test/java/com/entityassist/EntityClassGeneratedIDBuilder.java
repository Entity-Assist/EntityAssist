package com.entityassist;

import com.entityassist.querybuilder.QueryBuilderCore;
import com.google.inject.Key;
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
