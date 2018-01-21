package za.co.mmagon.entityassist;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import za.co.mmagon.guiceinjection.db.DBStartupAsync;

import javax.sql.DataSource;

public class TestDBStartup extends DBStartupAsync
{
	@Inject
	public TestDBStartup(@TestEntityAssistCustomPersistenceLoader PersistService ps, @TestEntityAssistCustomPersistenceLoader DataSource ds)
	{
		super(ps, ds);
		ps.start();
	}
}
