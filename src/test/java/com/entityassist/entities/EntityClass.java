package com.entityassist.entities;

import com.entityassist.entities.builders.EntityClassBuilder;
import com.entityassist.CoreEntity;

import javax.persistence.*;

@Entity
@Table
public class EntityClass
		extends CoreEntity<EntityClass, EntityClassBuilder, Long>
{
	@Id
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "entityClass")
	private EntityClassTwo entityClass;

	public EntityClass()
	{
	}

	public EntityClass(boolean blank)
	{
		super(blank);
	}

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

}