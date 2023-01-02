package com.entityassist;

import jakarta.persistence.*;

@Entity
@Table
public class EntityClassTwo
		extends CoreEntity<EntityClassTwo, EntityClassTwoBuilder, Long>
{
	@Id
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(referencedColumnName = "entityClass",
			name = "entityClass")
	private EntityClass entityClass;
	
	@Lob
	@Column(name = "dataColumn")
	private byte[] blob;

	public EntityClass getEntityClass()
	{
		return entityClass;
	}

	public void setEntityClass(EntityClass entityClass)
	{
		this.entityClass = entityClass;
	}

	@Override
	public String toString()
	{
		return "EntityClassTwoTest : " + getId();
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public EntityClassTwo setId(Long id)
	{
		this.id = id;
		return this;
	}
	
	public byte[] getBlob()
	{
		return blob;
	}
	
	public EntityClassTwo setBlob(byte[] blob)
	{
		this.blob = blob;
		return this;
	}
}
