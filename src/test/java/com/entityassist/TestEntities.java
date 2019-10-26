package com.entityassist;

import com.entityassist.entities.EntityClass;
import com.entityassist.entities.EntityClassGeneratedID;
import com.entityassist.enumerations.ActiveFlag;
import com.entityassist.enumerations.Operand;
import com.google.inject.Key;
import com.entityassist.entities.EntityClassTwo_;
import com.entityassist.entities.EntityClass_;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedpersistence.db.annotations.Transactional;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.entityassist.entities.EntityClass_.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestEntities
{
	@Test
	public void testMe()
	{
		System.out.println("Override for server builds?");
	}

	@Test
	public void testEntity()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());

		TestEntities te = GuiceContext.get(TestEntities.class);
		Optional<EntityClass> ec1 = new EntityClass().find(1L);
		System.out.println("ec : " + ec1);
	}

	@Test
	public void testEntity2Really()
	{
		GuiceContext.get(TestEntities.class)
		            .testEntity2();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testEntity2()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());

		EntityClass ec = new EntityClass();
		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();


		long l2;
		EntityClass ec2 = new EntityClass();
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		Optional<EntityClass> ec1 = new EntityClass().find(l);
		System.out.println("ec after find: " + ec1);

		System.out.println("Number of all rows : " + ec.builder()
		                                               .getCount());

		List<EntityClass> numberofresults = ec.builder()
		                                      .where(id, Operand.InList, l2)
		                                      .getAll();
		System.out.println("Wow that returned : " + numberofresults);
	}

	public long getNextNumber()
	{
		Optional<Long> max = new EntityClass().builder()
		                                      .selectMax(id)
		                                      .get(Long.class);
		return max.map(aLong -> aLong + 1)
		          .orElse(1L);
	}

	@Test
	public void testWhereInListReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testWhereInList();
	}

	private void configUp()
	{
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testWhereInList()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();

		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		long l2;
		EntityClass ec2 = new EntityClass();
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereEqualsReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testWhereEquals();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testWhereEquals()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.Equals, l)
		                     .getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testWhereGreaterThanEqualsReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testWhereGreaterThanEquals();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testWhereGreaterThanEquals()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();

		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.GreaterThanEqualTo, l)
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereGreaterReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testWhereGreater();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testWhereGreater()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();

		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.GreaterThan, l)
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testWhereLessThanEqualsReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testWhereLessThanEquals();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testWhereLessThanEquals()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.LessThanEqualTo, l2)
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereLessThanReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testWhereLessThan();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testWhereLessThan()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.LessThan, l2)
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testNotNullReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testNotNull();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testNotNull()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.NotNull, (Long) null)
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertTrue(resultCount == 2);
	}

	@Test
	public void testNullReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testNull();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testNull()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.Null, (Long) null)
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertTrue(0 == resultCount);
	}

	@Test
	public void testJoin()
	{
		configUp();
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
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
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .join(EntityClassTwo_.entityClass, JoinType.LEFT)
		                                          .getAll();
	}

	@Test
	public void testJoinLeftWithOnClauses()
	{
		configUp();
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
	}

	@Test
	public void testInRangeSpecified()
	{
		configUp();
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
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
	public void testGetAllReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testGetAll();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testGetAll()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .getAll();
	}

	@Test
	public void testDeleteReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testDelete();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testDelete()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		EntityClass ec = new EntityClass();
		long l;
		ec.setId(l = getNextNumber());
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		long l2;
		ec2.setId(l2 = getNextNumber());
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(l);
		resultList.add(l2);
		long resultCount = ec.builder()
		                     .where(id, Operand.Null, (Long) null)
		                     .where(id, Operand.InList, resultList)
		                     .getCount();
		assertTrue(0 == resultCount);

		ec.delete();
		ec2.delete();


	}

	@Test
	public void testOrCollectionReally()
	{
		configUp();
		GuiceContext.get(TestEntities.class)
		            .testOrCollection();
	}

	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testOrCollection()
	{
		EntityClassGeneratedID generatedID = new EntityClassGeneratedID();
		generatedID.builder()
		           .setRunDetached(true)
		           .persistNow(generatedID);

		//Test delete from builder
		EntityClassGeneratedID generatedID2 = new EntityClassGeneratedID();
		generatedID2.builder()
		            .persistNow(generatedID2);

		EntityClassGeneratedID generatedID3 = new EntityClassGeneratedID();
		generatedID3.builder()
		            .setRunDetached(true)
		            .persistNow(generatedID3);

		long resultCount = generatedID.builder()
		                              .where(id, Operand.Equals, generatedID.getId())
		                              .or(id, Operand.Equals, generatedID2.getId(), true)
		                              .or(id, Operand.Equals, generatedID3.getId())
		                              .where(id, Operand.NotNull, (Long) null)
		                              .getCount();

		assertTrue(3 == resultCount);
	}

	@Test
	public void testBulkUpdateReally()
	{
		GuiceContext.get(TestEntities.class)
		            .testBulkUpdate();
	}

	@SuppressWarnings("WeakerAccess")
	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testBulkUpdate()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		EntityClass updates = new EntityClass(true);
		updates.setActiveFlag(ActiveFlag.Archived);
		new EntityClass().builder()
		                 .where(EntityClass_.activeFlag, Operand.Equals, ActiveFlag.Invisible)
		                 .bulkUpdate(updates, true);
	}


	@Test
	public void testUpdateStatementReally()
	{
		GuiceContext.get(TestEntities.class)
		            .testUpdateStatement();
	}

	@SuppressWarnings("WeakerAccess")
	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	public void testUpdateStatement()
	{
		EntityManager em = GuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		EntityClass updates = new EntityClass(true);
		updates.setActiveFlag(ActiveFlag.Archived);
		new EntityClass().builder()
		                 .setRunDetached(true)
		                 .where(EntityClass_.activeFlag, Operand.Equals, ActiveFlag.Invisible)
		                 .bulkUpdate(updates, true);
	}
}
