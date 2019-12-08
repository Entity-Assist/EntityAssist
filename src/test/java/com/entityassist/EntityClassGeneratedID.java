package com.entityassist;

import javax.persistence.*;

@Entity
@Table
public class EntityClassGeneratedID
		extends CoreEntity<EntityClassGeneratedID, EntityClassGeneratedIDBuilder, Long>
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "entityClass")
	private EntityClassTwo entityClass;


	@OneToOne
	@JoinColumn(name = "entityClassOne")
	private EntityClass entityClassOne;

	public EntityClassGeneratedID()
	{
	}

	public EntityClassTwo getEntityClass()
	{
		return entityClass;
	}

	public void setEntityClass(EntityClassTwo entityClass)
	{
		this.entityClass = entityClass;
	}

	public EntityClass getEntityClassOne()
	{
		return entityClassOne;
	}

	public void setEntityClassOne(EntityClass entityClassOne)
	{
		this.entityClassOne = entityClassOne;
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
	public EntityClassGeneratedID setId(Long id)
	{
		this.id = id;
		return this;
	}

}
