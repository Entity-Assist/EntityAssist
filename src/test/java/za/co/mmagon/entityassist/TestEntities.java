package za.co.mmagon.entityassist;

import com.armineasy.injection.GuiceContext;
import org.junit.jupiter.api.Test;
import za.co.mmagon.entityassist.entities.EntityClass;
import za.co.mmagon.entityassist.enumerations.Operand;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static za.co.mmagon.entityassist.entities.EntityClass_.id;

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

		ec.persistNow();
		Optional<EntityClass> ec1 = new EntityClass().find(2L);
		System.out.println("ec : " + ec1);

		ec.builder().inVisibleRange();

		ec.builder()
				.selectCount()
				.selectCount(id)
				.selectColumn(id)
				.selectMax(id)
				.selectMin(id)
				.selectSum(id)
				.selectAverage(id)
				.selectSumAsLong(id)
				.selectSumAsDouble(id)

				.where(id, Operand.NotNull, null)
				.where(id, Operand.InList, 1)
				.where(id, Operand.MoreThanEqualTo, 1)

				.groupBy(id)
				.get()
		;
	}

	@Test
	public void testGetAll()
	{
		EntityManager em = GuiceContext.getInstance(EntityManager.class);
		System.out.println("EM Open : " + em.isOpen());

		EntityClass ec = new EntityClass();
		ec.setId(3L);

		ec.persistNow();

		List<EntityClass> list = ec.builder().getAll();
		if (list.size() < 1)
		{
			fail("Rows not inserted?");
		}
	}
}
