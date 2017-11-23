package za.co.mmagon.entityassist;

import com.armineasy.injection.GuiceContext;
import org.junit.jupiter.api.Test;
import za.co.mmagon.entityassist.entities.EntityClass;
import za.co.mmagon.entityassist.entities.EntityClass_;
import za.co.mmagon.entityassist.enumerations.Operand;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestEntities
{
	@Test
	public void testEntity()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		System.out.println("EM Open : " + em.isOpen());

		EntityClass ec = new EntityClass();
		ec.setId(1L);

		ec.persist();
		Optional<EntityClass> ec1 = new EntityClass().find(1L);
		System.out.println("ec : " + ec1);
	}

	@Test
	public void testEntity2()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		System.out.println("EM Open : " + em.isOpen());

		EntityClass ec = new EntityClass();
		ec.setId(2L);

		EntityClass ec2 = new EntityClass();
		ec2.setId(3L);

		ec.persistNow();
		ec2.persistNow();

		Optional<EntityClass> ec1 = new EntityClass().find(2L);
		System.out.println("ec after find: " + ec1);

		System.out.println("Number of all rows : " + ec.builder().selectCount().getCount());

		List<EntityClass> numberofresults = ec.builder()
				                                    // .selectCount()
				                                    //    .selectCountDistinct(EntityClass_.id)
				                      /* .selectColumn(EntityClass_.activeFlag)
				                       .selectMax(EntityClass_.id)
				                       .selectMin(EntityClass_.id)
				                       .selectSum(EntityClass_.id)
				                       .selectAverage(EntityClass_.id)
				                       .selectSumAsLong(EntityClass_.id)
				                       .selectSumAsDouble(EntityClass_.id)*/
				                                    //  .where(EntityClass_.id, Operand.NotNull, null)
				                                    .where(EntityClass_.id, Operand.InList, 2L)
				                                    //  .where(EntityClass_.id, Operand.MoreThanEqualTo, 2L)
				                                    //  .groupBy(EntityClass_.id)
				                                    .getAll()
				//.get();
				;
		System.out.println("Wow that returned : " + numberofresults);
	}

	@Test
	public void testWhereInList()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(4L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(5L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(4);
		resultList.add(5);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.InList, resultList).selectCount().getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereEquals()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(6L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(7L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(6);
		resultList.add(7);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.Equals, 6).selectCount().getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testWhereGreaterThanEquals()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(8L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(9L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(8);
		resultList.add(9);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.MoreThanEqualTo, 8)
				                   .where(EntityClass_.id, Operand.InList, resultList)
				                   .selectCount().getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereGreater()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(10L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(11L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(10);
		resultList.add(11);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.MoreThan, 10)
				                   .where(EntityClass_.id, Operand.InList, resultList)
				                   .selectCount().getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testWhereLessThanEquals()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(12L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(13L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(12);
		resultList.add(13);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.LessThanEqualTo, 13)
				                   .where(EntityClass_.id, Operand.InList, resultList).selectCount().getCount();
		assertEquals(2L, resultCount);
	}

	@Test
	public void testWhereLessThan()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(14L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(15L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(14);
		resultList.add(15);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.LessThan, 15)
				                   .where(EntityClass_.id, Operand.InList, resultList).selectCount().getCount();
		assertEquals(1L, resultCount);
	}

	@Test
	public void testNotNull()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(17L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(18L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(17);
		resultList.add(18);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.NotNull, null)
				                   .where(EntityClass_.id, Operand.InList, resultList).selectCount().getCount();
		assertTrue(resultCount == 2);
	}

	@Test
	public void testNull()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		EntityClass ec = new EntityClass();
		ec.setId(19L);
		ec.persistNow();

		EntityClass ec2 = new EntityClass();
		ec2.setId(20L);
		ec2.persistNow();

		List resultList = new ArrayList();
		resultList.add(20);
		resultList.add(21);
		long resultCount = ec.builder().where(EntityClass_.id, Operand.Null, null)
				                   .where(EntityClass_.id, Operand.InList, resultList).selectCount().getCount();
		assertTrue(0 == resultCount);
	}

	@Test
	public void testGetAll()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder().getAll();
		if (list.size() < 1)
		{
			fail("Rows not inserted?");
		}
	}
}
