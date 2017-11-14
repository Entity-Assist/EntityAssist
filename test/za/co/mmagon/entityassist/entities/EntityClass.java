package za.co.mmagon.entityassist.entities;

import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.entities.builders.EntityClassBuilder;

public class EntityClass extends CoreEntity<EntityClass, EntityClassBuilder, Long>
{
	private Long id;

	@Override
	public boolean isIdGenerated()
	{
		return true;
	}

	@Override
	public Long getId()
	{
		return id;
	}
	
	@Override
	public EntityClass setId(Long id)
	{
		return this;
	}
}
