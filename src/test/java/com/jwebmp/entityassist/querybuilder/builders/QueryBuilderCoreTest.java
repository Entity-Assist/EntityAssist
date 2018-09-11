package com.jwebmp.entityassist.querybuilder.builders;

import com.google.inject.Key;
import com.jwebmp.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.jwebmp.entityassist.entities.EntityClass;
import com.jwebmp.entityassist.entities.EntityClassTwo;
import com.jwebmp.entityassist.entities.EntityClassTwo_;
import com.jwebmp.entityassist.entities.EntityClass_;
import com.jwebmp.entityassist.enumerations.ActiveFlag;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedpersistence.db.annotations.Transactional;
import com.jwebmp.guicedpersistence.jpa.implementations.JPAAutomatedTransactionHandler;
import com.jwebmp.logger.LogFactory;
import com.jwebmp.logger.logging.LogColourFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.logging.Level;

import static com.jwebmp.entityassist.enumerations.Operand.*;
import static org.junit.jupiter.api.Assertions.*;

public class QueryBuilderCoreTest
{

	@BeforeEach
	public void before()
	{
		LogFactory.configureConsoleSingleLineOutput(Level.FINE);
		LogColourFormatter.setRenderBlack(false);
		GuiceContext.inject();
		JPAAutomatedTransactionHandler.setActive(true);
	}

	@Test
	public void testVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .join(EntityClassTwo_.entityClass, new EntityClassTwo().builder()
		                                                                                                 .where(EntityClassTwo_.activeFlag, Equals, ActiveFlag.Active))
		                                          .inVisibleRange()
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testDateRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange()
		                                          .join(EntityClassTwo_.entityClass)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testDateAndVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange()
		                                          .inVisibleRange()
		                                          .join(EntityClassTwo_.entityClass, null, JoinType.LEFT)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testDateAndVisibleRange1()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange()
		                                          .inVisibleRange()
		                                          .join(EntityClassTwo_.entityClass, new EntityClassTwo().builder()
		                                                                                                 .where(EntityClassTwo_.activeFlag, Equals, ActiveFlag.Active)
		                                                                                                 .inActiveRange(), JoinType.LEFT)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testBulkUpdateReally()
	{
		GuiceContext.get(QueryBuilderCoreTest.class)
		            .testBulkUpdate();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testBulkUpdate()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		EntityClass updates = new EntityClass(true);
		updates.setActiveFlag(ActiveFlag.Archived);
		new EntityClass().builder()
		                 .where(EntityClass_.activeFlag, Equals, ActiveFlag.Invisible)
		                 .bulkUpdate(updates, true);
	}
}
