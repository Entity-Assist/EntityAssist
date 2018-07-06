package com.jwebmp.entityassist.entities;

import com.jwebmp.entityassist.CoreEntity_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EntityClassTwo.class)
public abstract class EntityClassTwo_
		extends CoreEntity_
{
	public static volatile SingularAttribute<EntityClassTwo, EntityClass> entityClass;
	public static volatile SingularAttribute<EntityClassTwo, Long> id;

}

