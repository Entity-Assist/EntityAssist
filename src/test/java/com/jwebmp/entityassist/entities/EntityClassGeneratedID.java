package com.jwebmp.entityassist.entities;

import com.jwebmp.entityassist.CoreEntity;
import com.jwebmp.entityassist.entities.builders.EntityClassGeneratedIDBuilder;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

	public EntityClassGeneratedID(boolean blank)
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
