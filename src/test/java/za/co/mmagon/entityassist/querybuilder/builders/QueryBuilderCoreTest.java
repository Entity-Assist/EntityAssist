package za.co.mmagon.entityassist.querybuilder.builders;

import com.google.inject.Key;
import org.junit.jupiter.api.Test;
import za.co.mmagon.entityassist.TestEntityAssistCustomPersistenceLoader;
import za.co.mmagon.entityassist.entities.EntityClass;
import za.co.mmagon.entityassist.entities.EntityClassTwo;
import za.co.mmagon.entityassist.entities.EntityClassTwo_;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.enumerations.Operand;
import za.co.mmagon.guiceinjection.GuiceContext;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class QueryBuilderCoreTest
{


	@Test
	public void testVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
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
		                                                                                                 .where(EntityClassTwo_.activeFlag, Operand.Equals, ActiveFlag.Active)
		                                                                                                 .inActiveRange(), JoinType.LEFT)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testBulkUpdate()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		EntityClass updates = new EntityClass(true);
		updates.setActiveFlag(ActiveFlag.Archived);
		new EntityClass().builder()
		                 .bulkUpdate(updates, true);
	}
}
