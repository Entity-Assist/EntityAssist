package com.jwebmp.entityassist.entities;

import com.jwebmp.entityassist.CoreEntity_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(EntityClass.class)
public abstract class EntityClass_
		extends CoreEntity_
{

	public static volatile SingularAttribute<EntityClass, EntityClassTwo> entityClass;
	public static volatile SingularAttribute<EntityClass, Long> id;

}

