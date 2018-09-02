package com.jwebmp.entityassist;

import com.jwebmp.guicedinjection.interfaces.IGuiceModule;
import com.jwebmp.guicedpersistence.db.AbstractDatabaseProviderModule;
import com.jwebmp.guicedpersistence.db.ConnectionBaseInfo;
import com.jwebmp.guicedpersistence.jpa.JPAConnectionBaseInfo;
import com.oracle.jaxb21.PersistenceUnit;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.Properties;

public class EntityAssistTestDBModule
		extends AbstractDatabaseProviderModule
		implements IGuiceModule
{

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

	@Override
	protected String getJndiMapping()
	{
		return "jdbc:eatest";
	}

	@Override
	protected Class<? extends Annotation> getBindingAnnotation()
	{
		return TestEntityAssistCustomPersistenceLoader.class;
	}
}
