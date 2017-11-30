package za.co.mmagon.entityassist.entities;

import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.entities.builders.EntityClassBuilder;

import javax.persistence.*;

@Entity
@Table
public class EntityClass extends CoreEntity<EntityClass, EntityClassBuilder, Long>
{
	@Id
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "entityClass")
	private EntityClassTwo entityClass;

	public EntityClassTwo getEntityClass()
	{
		return entityClass;
	}

	public void setEntityClass(EntityClassTwo entityClass)
	{
		this.entityClass = entityClass;
	}

	@Override
	public String toString()
	{
		return "EntityTest : " + getId();
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public EntityClass setId(Long id)
	{
		this.id = id;
		return this;
	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}
}
