package za.co.mmagon.entityassist.sessions;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;

public class NoLockSessionCustomizer implements org.eclipse.persistence.config.SessionCustomizer
{
	@Override
	public void customize(Session session) throws Exception
	{
		DatabaseLogin databaseLogin = (DatabaseLogin) session.getDatasourceLogin();
		databaseLogin.setTransactionIsolation(DatabaseLogin.TRANSACTION_READ_COMMITTED);
	}

}
