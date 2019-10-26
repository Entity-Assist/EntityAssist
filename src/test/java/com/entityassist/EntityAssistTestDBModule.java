package com.entityassist;

import com.guicedee.guicedpersistence.db.DatabaseModule;
import com.guicedee.guicedpersistence.db.ConnectionBaseInfo;
import com.oracle.jaxb21.PersistenceUnit;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.Properties;

public class EntityAssistTestDBModule
		extends DatabaseModule<EntityAssistTestDBModule>
{

	@NotNull
	@Override
	protected String getPersistenceUnitName()
	{
		return "h2entityAssist";
	}

	@Override
	@NotNull
	protected ConnectionBaseInfo getConnectionBaseInfo(PersistenceUnit unit, Properties filteredProperties)
	{
		return new JPAConnectionBaseInfo();
	}

	@NotNull
	@Override
	protected String getJndiMapping()
	{
		return "jdbc:eatest";
	}

	@NotNull
	@Override
	protected Class<? extends Annotation> getBindingAnnotation()
	{
		return TestEntityAssistCustomPersistenceLoader.class;
	}
}
