package com.jwebmp.entityassist;

import com.google.inject.Key;
import com.jwebmp.entityassist.entities.EntityClass;
import com.jwebmp.entityassist.entities.EntityClassTwo_;
import com.jwebmp.entityassist.entities.EntityClass_;
import com.jwebmp.entityassist.enumerations.Operand;
import com.jwebmp.guicedinjection.GuiceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestEntities
{
	@BeforeAll
	public static void before()
	{
		GuiceContext.inject();
	}

	@Test
	public void testMe()
	{
		System.out.println("Override for server builds?");
	}

	@Test
	public void testEntity()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());

		TestEntities te = GuiceContext.getInstance(TestEntities.class);
		Optional<EntityClass> ec1 = new EntityClass().find(1L);
		System.out.println("ec : " + ec1);
	}

	@Test
	public void testEntity2()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());

		EntityClass ec = new EntityClass();
		ec.setId(2L);

		EntityClass ec2 = new EntityClass();
		ec2.setId(3L);

		ec.persistNow();
		ec2.persistNow();

		Optional<EntityClass> ec1 = new EntityClass().find(2L);
		System.out.println("ec after find: " + ec1);

		System.out.println("Number of all rows : " + ec.builder()
		                                               .getCount());

		List<EntityClass> numberofresults = ec.builder()
		                                      .where(EntityClass_.id, Operand.InList, 2L)
		                                      .getAll();
		System.out.println("Wow that returned : " + numberofresults);
	}

	@Test
	public void testWhereInList()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(4L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(5L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(4);
		resultList.add(5);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereEquals()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(6L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(7L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(6);
		resultList.add(7);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.Equals, 6L)
		                     .getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testWhereGreaterThanEquals()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(8L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(9L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(8);
		resultList.add(9);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.GreaterThanEqualTo, 8L)
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereGreater()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(10L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(11L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(10);
		resultList.add(11);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.GreaterThan, 10L)
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testWhereLessThanEquals()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(12L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(13L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(12);
		resultList.add(13);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.LessThanEqualTo, 13L)
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereLessThan()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(14L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(15L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(14);
		resultList.add(15);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.LessThan, 15L)
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testNotNull()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(17L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(18L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(17);
		resultList.add(18);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.NotNull, (Long) null)
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertTrue(resultCount == 2);
	}

	@Test
	public void testNull()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(19L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(20L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(20);
		resultList.add(21);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.Null, (Long) null)
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertTrue(0 == resultCount);
	}

	@Test
	public void testGetAll()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .getAll();
	}

	@Test
	public void testJoin()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .join(EntityClassTwo_.entityClass)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testJoinLeft()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .join(EntityClassTwo_.entityClass, JoinType.LEFT)
		                                          .getAll();
	}

	@Test
	public void testInRangeSpecified()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange(LocalDateTime.now(), LocalDateTime.now())
		                                          .join(EntityClassTwo_.entityClass, JoinType.LEFT)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}
}
