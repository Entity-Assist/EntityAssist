package com.test.querybuilder.builders;

import com.entityassist.enumerations.ActiveFlag;
import com.entityassist.enumerations.Operand;
import com.google.inject.Key;
import com.guicedee.client.IGuiceContext;
import com.test.EntityClass;
import com.test.EntityClassTwo;
import com.test.EntityClassTwo_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.JoinType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryBuilderCoreTest
{
    @Test
    public void testVisibleRange()
    {


        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
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
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
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
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
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
        EntityManager em = IGuiceContext.get(Key.get(EntityManager.class));
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
