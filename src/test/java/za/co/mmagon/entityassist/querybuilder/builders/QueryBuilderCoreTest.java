package za.co.mmagon.entityassist.querybuilder.builders;

public class QueryBuilderCoreTest
{
/*

	@Test
	public void testVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
				                         .join(EntityClassTwo_.entityClass,
				                               new EntityClassTwo().builder()
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
				                         .join(EntityClassTwo_.entityClass,
				                               new EntityClassTwo().builder()
						                               .where(EntityClassTwo_.activeFlag, Operand.Equals, ActiveFlag.Active)
						                         .inActiveRange(),
				                               JoinType.LEFT)
				                         .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}*/
}
