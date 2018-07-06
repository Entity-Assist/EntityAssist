package com.jwebmp.entityassist;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.io.Serializable;

@StaticMetamodel(BaseEntity.class)
public abstract class BaseEntity_
{

	public static volatile SingularAttribute<BaseEntity, Serializable> id;
	public static volatile SingularAttribute<BaseEntity, Class> classQueryBuilderClass;
	public static volatile SingularAttribute<BaseEntity, Class> classIDType;
	public static volatile MapAttribute<BaseEntity, Serializable, Serializable> properties;

}

