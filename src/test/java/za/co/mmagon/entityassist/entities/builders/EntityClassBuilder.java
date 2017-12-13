package za.co.mmagon.entityassist.entities.builders;

import za.co.mmagon.entityassist.entities.EntityClass;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;
import za.co.mmagon.guiceinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassBuilder extends QueryBuilderCore<EntityClassBuilder, EntityClass, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(EntityManager.class);
	}

	@Override
	protected void onCreate()
	{

	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}
}
