package za.co.mmagon.entityassist.entities.builders;

import com.google.inject.Key;
import za.co.mmagon.entityassist.TestEntityAssistCustomPersistenceLoader;
import za.co.mmagon.entityassist.entities.EntityClass;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;
import za.co.mmagon.guiceinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassBuilder extends QueryBuilderCore<EntityClassBuilder, EntityClass, Long>
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
	protected void onCreate(EntityClass entity)
	{

	}

	@Override
	protected void onUpdate(EntityClass entity)
	{

	}


	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}
}
