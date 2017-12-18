package za.co.mmagon.entityassist.querybuilder;

public class QueryBuilderCoreTest
{

	/*
	@Test
	public void testVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
				                         .inVisibleRange()
				                         .join(za.co.mmagon.entityassist.entities.EntityClassTwo_.entityClass, JoinType.LEFT)
				                         .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}*/

	/*

	@Test
	public void testDateRange()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
				                         .inDateRange()
				                         .join(EntityClassTwo_.entityClass, JoinType.LEFT)
				                         .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testDateAndVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
				                         .inDateRange()
				                         .inVisibleRange()
				                         .join(EntityClassTwo_.entityClass, JoinType.LEFT)
				                         .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}
	*/
}
