package com.test;

import com.guicedee.guicedpersistence.db.ConnectionBaseInfo;
import com.guicedee.guicedpersistence.db.DatabaseModule;
import com.guicedee.guicedpersistence.jta.JPAConnectionBaseInfo;
import jakarta.validation.constraints.NotNull;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;

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
	protected ConnectionBaseInfo getConnectionBaseInfo(ParsedPersistenceXmlDescriptor unit, Properties filteredProperties)
	{
		return new JPAConnectionBaseInfo();
	}

	@NotNull
	@Override
	protected String getJndiMapping()
	{
		return "jdbc:eatest";
	}
}
