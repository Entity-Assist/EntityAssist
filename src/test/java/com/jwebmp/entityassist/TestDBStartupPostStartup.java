package com.jwebmp.entityassist;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.jwebmp.guicedpersistence.services.IDBStartup;

import javax.sql.DataSource;

public class TestDBStartupPostStartup
		implements IDBStartup
{
	@Inject
	public TestDBStartupPostStartup(@TestEntityAssistCustomPersistenceLoader PersistService ps, @TestEntityAssistCustomPersistenceLoader DataSource ds)
	{
		ps.start();
	}

}
