package com.jwebmp.entityassist;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.jwebmp.guicedinjection.interfaces.IGuicePostStartup;

public class TestDBStartupPostStartup
		implements IGuicePostStartup
{
	public TestDBStartupPostStartup()
	{
	}

	@Inject
	public TestDBStartupPostStartup(@TestEntityAssistCustomPersistenceLoader PersistService ps)
	{
		ps.start();
	}

	@Override
	public void postLoad()
	{

	}
}
