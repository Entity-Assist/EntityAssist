package za.co.mmagon.entityassist.entities.builders;

import com.google.inject.Key;
import za.co.mmagon.entityassist.TestEntityAssistCustomPersistenceLoader;
import za.co.mmagon.entityassist.entities.EntityClassTwo;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;
import za.co.mmagon.guiceinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassTwoBuilder extends QueryBuilderCore<EntityClassTwoBuilder, EntityClassTwo, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}

	protected void onCreate(EntityClassTwo entity)
	{

	}

	@Override
	protected void onUpdate(EntityClassTwo entity)
	{

	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}
}
