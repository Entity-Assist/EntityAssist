package com.jwebmp.entityassist;

import com.jwebmp.guiceinjection.db.ConnectionBaseInfo;
import com.jwebmp.guiceinjection.db.connectionbasebuilders.HibernateDefaultConnectionBaseBuilder;
import com.oracle.jaxb21.Persistence;

import java.lang.annotation.Annotation;
import java.util.Properties;

public class EntityAssistTestDBModule
		extends HibernateDefaultConnectionBaseBuilder
{

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

	@Override
	protected ConnectionBaseInfo getConnectionBaseInfo(Persistence.PersistenceUnit unit, Properties filteredProperties)
	{

		return super.getConnectionBaseInfo(unit, filteredProperties)
		            .setAllowLocalTransactions(true);
	}
}
