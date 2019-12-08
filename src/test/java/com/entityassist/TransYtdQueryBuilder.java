package com.entityassist;

import com.entityassist.querybuilder.QueryBuilder;
import com.google.inject.Key;
import com.guicedee.guicedinjection.GuiceContext;

import javax.persistence.EntityManager;

public class TransYtdQueryBuilder
		extends QueryBuilder<TransYtdQueryBuilder, TransYtd, TransYtdPK>
{

	protected boolean onCreate(EntityClass entity)
	{
		return true;
	}


	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}


	@Override
	protected boolean isIdGenerated()
	{
		return false;
	}

	protected boolean onUpdate(EntityClass entity)
	{
		return true;
	}
}
