package com.entityassist;

import com.guicedee.guicedpersistence.db.ConnectionBaseInfo;


import javax.sql.DataSource;
import java.util.logging.Logger;

public class JPAConnectionBaseInfo
		extends ConnectionBaseInfo
{
	private boolean driverRegistered;

	/**
	 * You can fetch it directly from the entity manager using (DataSource)managerFactory.getConnectionFactory()
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public DataSource toPooledDatasource()
	{
		return null;
	}
}
