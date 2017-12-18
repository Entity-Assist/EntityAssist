package za.co.mmagon.entityassist;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

import javax.sql.DataSource;

public class TestDBStartup extends za.co.mmagon.guiceinjection.db.DBStartupAsync
{
	@Inject
	public TestDBStartup(@TestEntityAssistCustomPersistenceLoader PersistService ps, @TestEntityAssistCustomPersistenceLoader DataSource ds)
	{
		ps.start();
	}
}
