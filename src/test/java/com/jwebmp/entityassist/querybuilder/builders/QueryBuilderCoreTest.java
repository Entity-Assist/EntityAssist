package com.guicedee.entityassist.querybuilder.builders;

import com.google.inject.Key;
import com.guicedee.entityassist.enumerations.ActiveFlag;
import com.guicedee.entityassist.enumerations.Operand;
import com.guicedee.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.guicedee.entityassist.entities.EntityClass;
import com.guicedee.entityassist.entities.EntityClassTwo;
import com.guicedee.jpms.entityassist.entities.EntityClassTwo_;
import com.guicedee.guicedinjection.GuiceContext;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryBuilderCoreTest
{
	@Test
	public void testVisibleRange()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .join(EntityClassTwo_.entityClass, new EntityClassTwo().builder()
		                                                                                                 .where(EntityClassTwo_.activeFlag, Operand.Equals, ActiveFlag.Active))
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
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
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
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
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
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange()
		                                          .inVisibleRange()
		                                          .join(EntityClassTwo_.entityClass, new EntityClassTwo().builder()
		                                                                                                 .where(EntityClassTwo_.activeFlag, Operand.Equals, ActiveFlag.Active)
		                                                                                                 .inActiveRange(), JoinType.LEFT)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}
}
