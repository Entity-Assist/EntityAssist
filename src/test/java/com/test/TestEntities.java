package com.test;

import com.entityassist.RootEntity;
import com.entityassist.enumerations.Operand;
import com.google.inject.Key;
import com.google.inject.persist.Transactional;
import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.GuiceContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.test.EntityClass_.id;
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
        //	LogFactory.configureConsoleColourOutput(Level.FINE);
        GuiceContext.instance()
                    .loadIGuiceModules()
                    .add(new EntityAssistTestDBModule());
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        System.out.println("EM Open : " + em.isOpen());

        TestEntities te = IGuiceContext.get(TestEntities.class);
        Optional<EntityClass> ec1 = new EntityClass().find(1L);
        System.out.println("ec : " + ec1);
    }

    @Test
    public void testEntity2Really()
    {
        //	LogFactory.configureConsoleColourOutput(Level.FINE);
        GuiceContext.instance()
                    .loadIGuiceModules()
                    .add(new EntityAssistTestDBModule());
        IGuiceContext.get(TestEntities.class)
                     .testEntity2();
    }

    @Transactional()
    public void testEntity2()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        System.out.println("EM Open : " + em.isOpen());

        EntityClass ec = new EntityClass();
        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        long l2;
        EntityClass ec2 = new EntityClass();
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

        Optional<EntityClass> ec1 = new EntityClass().find(l);
        System.out.println("ec after find: " + ec1);

        System.out.println("Number of all rows : " + ec.builder()
                                                       .getCount());

        List<EntityClass> numberofresults = ec.builder()
                                              .where(id, Operand.InList, l2)
                                              .getAll();
        System.out.println("Wow that returned : " + numberofresults);
    }

    @Test
    public void testEntityEmbeddableID()
    {
        //	LogFactory.configureConsoleColourOutput(Level.FINE);
        GuiceContext.instance()
                    .loadIGuiceModules()
                    .add(new EntityAssistTestDBModule());
        IGuiceContext.get(TestEntities.class)
                     .testEntityEmbeddableIDReally();
    }

    @Transactional()
    public void testEntityEmbeddableIDReally()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        System.out.println("EM Open : " + em.isOpen());

        TransYtd ytd = new TransYtd();
        ytd.setId(new TransYtdPK().setDayID(1)
                                  .setYtdDayID(1));
        ytd.persist();

        Long numberofresults = ytd.builder()
                                  .getCount();
        System.out.println("Wow that returned : " + numberofresults);

        ytd = new TransYtd();
        ytd.setId(new TransYtdPK().setDayID(2)
                                  .setYtdDayID(2));
        ytd.builder()
           //.setRunDetached(true)
           .persist(ytd);

        numberofresults = ytd.builder()
                             .getCount();
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
        IGuiceContext.get(TestEntities.class)
                     .testWhereInList();
    }

    private void configUp()
    {
        //LogFactory.configureConsoleColourOutput(Level.FINE);
        GuiceContext.instance()
                    .loadIGuiceModules()
                    .add(new EntityAssistTestDBModule());
    }

    @Transactional()
    public void testWhereInList()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();

        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        long l2;
        EntityClass ec2 = new EntityClass();
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testWhereEquals();
    }

    @Transactional()
    public void testWhereEquals()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();
        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testWhereGreaterThanEquals();
    }

    @Transactional()
    public void testWhereGreaterThanEquals()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();

        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testWhereGreater();
    }

    @Transactional()
    public void testWhereGreater()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();

        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testWhereLessThanEquals();
    }

    @Transactional()
    public void testWhereLessThanEquals()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();
        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testWhereLessThan();
    }

    @Transactional()
    public void testWhereLessThan()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();
        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testNotNull();
    }

    @Transactional()
    public void testNotNull()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();
        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testNull();
    }

    @Transactional()
    public void testNull()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();
        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
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
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        System.out.println("EM Open : " + em.isOpen());
        List<EntityClass> list = new EntityClass().builder()
                                                  .join(EntityClassTwo_.entityClass, JoinType.LEFT)
                                                  .getAll();
    }

    @Test
    public void testJoinLeftWithOnClauses()
    {
        configUp();
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        System.out.println("EM Open : " + em.isOpen());
    }

    @Test
    public void testInRangeSpecified()
    {
        configUp();
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        System.out.println("EM Open : " + em.isOpen());
        List<EntityClass> list = new EntityClass().builder()
                                                  .inDateRange(RootEntity.getNow(), RootEntity.getNow())
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
        IGuiceContext.get(TestEntities.class)
                     .testGetAll();
    }

    @Transactional()
    public void testGetAll()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        System.out.println("EM Open : " + em.isOpen());
        List<EntityClass> list = new EntityClass().builder()
                                                  .getAll();
    }

    @Test
    public void testDeleteReally()
    {
        configUp();
        IGuiceContext.get(TestEntities.class)
                     .testDelete();
    }

    @Transactional()
    public void testDelete()
    {
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
        EntityClass ec = new EntityClass();
        long l;
        ec.setId(l = getNextNumber());
        ec.persist();

        EntityClass ec2 = new EntityClass();
        long l2;
        ec2.setId(l2 = getNextNumber());
        ec2.persist();

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
        IGuiceContext.get(TestEntities.class)
                     .testOrCollection();
    }

    @Transactional()
    public void testOrCollection()
    {
        EntityClassGeneratedID generatedID = new EntityClassGeneratedID();
        generatedID.builder()
                   //.setRunDetached(true)
                   .persist(generatedID);

        //Test delete from builder
        EntityClassGeneratedID generatedID2 = new EntityClassGeneratedID();
        generatedID2.builder()
                    .persist(generatedID2);

        EntityClassGeneratedID generatedID3 = new EntityClassGeneratedID();
        generatedID3.builder()
                    //.setRunDetached(true)
                    .persist(generatedID3);

        long resultCount = generatedID.builder()
                                      .where(id, Operand.Equals, generatedID.getId())
                                      .or(id, Operand.Equals, generatedID2.getId(), true)
                                      .or(id, Operand.Equals, generatedID3.getId())
                                      .where(id, Operand.NotNull, (Long) null)
                                      .getCount();

        assertTrue(3 == resultCount);
    }

}
