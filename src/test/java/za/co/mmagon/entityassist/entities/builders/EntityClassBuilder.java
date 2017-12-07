package za.co.mmagon.entityassist.entities.builders;

import com.armineasy.injection.GuiceContext;
import za.co.mmagon.entityassist.entities.EntityClass;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;

import javax.persistence.EntityManager;

public class EntityClassBuilder extends QueryBuilderCore<EntityClassBuilder, EntityClass, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(EntityManager.class);
	}
}
