package za.co.mmagon.entityassist.entities.builders;

import com.armineasy.injection.GuiceContext;
import za.co.mmagon.entityassist.entities.EntityClassTwo;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;

import javax.persistence.EntityManager;

public class EntityClassTwoBuilder extends QueryBuilderCore<EntityClassTwoBuilder, EntityClassTwo, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(EntityManager.class);
	}
}
