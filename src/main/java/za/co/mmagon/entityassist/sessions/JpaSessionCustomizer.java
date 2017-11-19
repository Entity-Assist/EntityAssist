package za.co.mmagon.entityassist.sessions;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;

import java.beans.PropertyVetoException;
import java.util.logging.Logger;

public class JpaSessionCustomizer implements SessionCustomizer
{

	private static final Logger log = Logger.getLogger(JpaSessionCustomizer.class.getName());

	@Override
	public void customize(Session session) throws Exception
	{
		DatabaseLogin databaseLogin = session.getLogin();
		databaseLogin.setTransactionIsolation(DatabaseLogin.TRANSACTION_READ_COMMITTED);

		String jdbcDriver = databaseLogin.getDriverClassName();
		String jdbcUrl = databaseLogin.getDatabaseURL();
		String username = databaseLogin.getUserName();
		// WARNING: databaseLogin.getPassword() is encrypted,
		// which cannot be used directly here
		String password = "Override with system properies c3p0.password xD";
		log.config(String.format("jdbcDriver={}, jdbcUrl={}, username={}, password={}",
		                         jdbcDriver, jdbcUrl, username, password));

		ComboPooledDataSource dataSource = buildDataSource(jdbcDriver, jdbcUrl, username, password);
		databaseLogin.setConnector(new JNDIConnector(dataSource));
	}

	private ComboPooledDataSource buildDataSource(String jdbcDriver,
	                                              String jdbcUrl,
	                                              String username,
	                                              String password) throws PropertyVetoException
	{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();

		dataSource.setDriverClass(jdbcDriver); // Loads the JDBC driver
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setUser(username);
		dataSource.setPassword(password);

		dataSource.setUnreturnedConnectionTimeout(15);
		dataSource.setAcquireRetryAttempts(10);

		dataSource.setInitialPoolSize(10);
		dataSource.setPreferredTestQuery("SELECT 1");
		dataSource.setTestConnectionOnCheckin(true);
		dataSource.setAcquireIncrement(2);

		dataSource.setIdleConnectionTestPeriod(300);
		dataSource.setMaxIdleTimeExcessConnections(240);

		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setMaxIdleTime(1800);

		dataSource.setMaxStatements(180);

		dataSource.setMinPoolSize(2);

		return dataSource;
	}
}
