package com.jwebmp.entityassist;

import com.jwebmp.guicedpersistence.db.ConnectionBaseInfo;
import com.jwebmp.guicedpersistence.db.connectionbasebuilders.AbstractDatabaseProviderModule;
import com.oracle.jaxb21.PersistenceUnit;

import java.lang.annotation.Annotation;
import java.util.Properties;

public class EntityAssistTestDBModule
		extends AbstractDatabaseProviderModule
{

	@Override
	protected ConnectionBaseInfo getConnectionBaseInfo(PersistenceUnit unit, Properties filteredProperties)
	{
		return new com.jwebmp.guicedpersistence.jpa.JPAConnectionBaseInfo().setXa(false);
	}

	@Override
	protected String getJndiMapping()
	{
		return "jdbc:eatest";
	}

	@Override
	protected String getPersistenceUnitName()
	{
		return "h2entityAssist";
	}

	@Override
	protected Class<? extends Annotation> getBindingAnnotation()
	{
		return TestEntityAssistCustomPersistenceLoader.class;
	}
}
