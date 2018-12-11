package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.guicedpersistence.services.ITransactionHandler;
import com.oracle.jaxb21.PersistenceUnit;

import javax.persistence.EntityManager;

public class JPAAutomatedTransactionHandler
		implements ITransactionHandler<JPAAutomatedTransactionHandler>
{
	private static boolean active = true;

	public JPAAutomatedTransactionHandler()
	{
		//No config required
	}

	/**
	 * Sets this Automated transaction handler to active
	 *
	 * @param active
	 */
	public static void setActive(boolean active)
	{
		JPAAutomatedTransactionHandler.active = active;
	}

	@Override
	public void beginTransacation(boolean createNew, EntityManager entityManager, PersistenceUnit persistenceUnit)
	{
		entityManager.getTransaction()
		             .begin();

	}

	@Override
	public void commitTransacation(boolean createNew, EntityManager entityManager, PersistenceUnit persistenceUnit)
	{
		entityManager.getTransaction()
		             .commit();
	}

	@Override
	public void rollbackTransacation(boolean createNew, EntityManager entityManager, PersistenceUnit persistenceUnit)
	{
		entityManager.getTransaction()
		             .rollback();
	}

	@Override
	public boolean transactionExists(EntityManager entityManager, PersistenceUnit persistenceUnit)
	{
		return entityManager.getTransaction()
		                    .isActive();
	}

	@Override
	public boolean active(PersistenceUnit persistenceUnit)
	{
		return active && persistenceUnit.getTransactionType() == null || persistenceUnit.getTransactionType()
		                                                                                .equals("RESOURCE_LOCAL");
	}
}
