package com.jwebmp.entityassist;

import com.google.inject.Key;
import com.jwebmp.entityassist.entities.*;
import com.jwebmp.entityassist.enumerations.Operand;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedpersistence.db.annotations.Transactional;
import com.jwebmp.logger.LogFactory;
import com.jwebmp.logger.logging.LogColourFormatter;
import com.jwebmp.testing.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jwebmp.entityassist.enumerations.Operand.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestEntities
		extends BaseTest
{
	private static final Logger log = LogFactory.getLog(TestEntities.class);

	private static TestEntities testEntities;

	@Test
	public void testMe()
	{
		configUp();
		System.out.println("Override for server builds?");
	}

	private void configUp()
	{
		LogFactory.configureConsoleColourOutput(Level.FINE);
		LogColourFormatter.setRenderBlack(false);
		System.setErr(System.out);
		log.config("Starting Up Instance");
		testEntities = GuiceContext.get(TestEntities.class);
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
	public void testEntity2Really()
	{
		configUp();
		testEntities.testEntity2();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testWhereInListReally()
	{
		configUp();
		testEntities.testWhereInList();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testWhereEqualsReally()
	{
		configUp();
		testEntities.testWhereEquals();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
		                     .where(EntityClass_.id, Equals, 6L)
		                     .getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testWhereGreaterThanEqualsReally()
	{
		configUp();
		testEntities.testWhereGreaterThanEquals();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testWhereGreaterReally()
	{
		configUp();
		testEntities.testWhereGreater();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testWhereLessThanEqualsReally()
	{
		configUp();
		testEntities.testWhereLessThanEquals();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testWhereLessThanReally()
	{
		configUp();
		testEntities.testWhereLessThan();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testNotNullReally()
	{
		configUp();
		testEntities.testNotNull();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testNullReally()
	{
		configUp();
		testEntities.testNull();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
	public void testGetAllReally()
	{
		configUp();
		testEntities.testGetAll();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
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
		configUp();
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
		configUp();
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .join(EntityClassTwo_.entityClass, JoinType.LEFT)
		                                          .getAll();
	}

	@Test
	public void testInRangeSpecified()
	{
		configUp();
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

	@Test
	public void testDeleteReally()
	{
		configUp();
		testEntities.testDelete();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testDelete()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		ec.setId(21L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(22L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(21L);
		resultList.add(22L);
		long resultCount = ec.builder()
		                     .where(EntityClass_.id, Operand.Null, (Long) null)
		                     .where(EntityClass_.id, Operand.InList, resultList)
		                     .getCount();
		assertTrue(0 == resultCount);

		EntityClassGeneratedID generatedID = new EntityClassGeneratedID();
		generatedID.builder()
		           .setRunDetached(true)
		           .persist(generatedID);
		generatedID.delete();


		//Test delete from builder
		EntityClassGeneratedID generatedID2 = new EntityClassGeneratedID();
		generatedID2.builder()
		            .setRunDetached(true)
		            .persist(generatedID2);

		generatedID2.builder()
		            .where(EntityClassGeneratedID_.id, Equals, generatedID2.getId())
		            .delete();
	}

	@Test
	public void testOrCollectionReally()
	{
		configUp();
		testEntities.testOrCollection();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testOrCollection()
	{

		EntityClassGeneratedID generatedID = new EntityClassGeneratedID();
		generatedID.builder()
		           .setRunDetached(true)
		           .persist(generatedID);

		//Test delete from builder
		EntityClassGeneratedID generatedID2 = new EntityClassGeneratedID();
		generatedID2.builder()
		            .setRunDetached(true)
		            .persist(generatedID2);

		EntityClassGeneratedID generatedID3 = new EntityClassGeneratedID();
		generatedID3.builder()
		            .setRunDetached(true)
		            .persist(generatedID3);

		long resultCount = generatedID.builder()
		                              .where(EntityClass_.id, Operand.Equals, generatedID.getId())
		                              .or(EntityClass_.id, Operand.Equals, generatedID2.getId(), true)
		                              .or(EntityClass_.id, Operand.Equals, generatedID3.getId())
		                              .where(EntityClass_.id, NotNull, (Long) null)
		                              .getCount();

		assertTrue(3 == resultCount);
	}
}
