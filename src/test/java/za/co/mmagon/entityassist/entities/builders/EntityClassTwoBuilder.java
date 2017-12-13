package za.co.mmagon.entityassist.entities.builders;

import za.co.mmagon.entityassist.entities.EntityClassTwo;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;
import za.co.mmagon.guiceinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassTwoBuilder extends QueryBuilderCore<EntityClassTwoBuilder, EntityClassTwo, Long>
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
