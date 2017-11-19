package za.co.mmagon.entityassist.entities;

import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.entities.builders.EntityClassBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class EntityClass extends CoreEntity<EntityClass, EntityClassBuilder, Long>
{
	@Id
	@Column(name = "id")
	private Long id;

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}

	@Override
	public EntityClass setId(Long id)
	{
		this.id = id;
		return this;
	}

	@Override
	public String toString()
	{
		return "EntityTest : " + getId();
	}
}
