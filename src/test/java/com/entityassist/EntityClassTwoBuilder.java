package com.entityassist;

import com.entityassist.querybuilder.QueryBuilderCore;
import com.google.inject.Key;
import com.guicedee.guicedinjection.GuiceContext;

import jakarta.persistence.EntityManager;

public class EntityClassTwoBuilder
		extends QueryBuilderCore<EntityClassTwoBuilder, EntityClassTwo, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}
	
	@Override
	public boolean onCreate(EntityClassTwo entity)
	{
		return true;
	}
	
	@Override
	public boolean isIdGenerated()
	{
		return false;
	}
	
	@Override
	public boolean onUpdate(EntityClassTwo entity)
	{
		return true;
	}
}
