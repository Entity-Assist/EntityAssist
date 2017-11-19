package za.co.mmagon.entityassist.querybuilder;

import com.armineasy.injection.GuiceContext;
import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.querybuilder.builders.QueryBuilderExecutor;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @param <J>
 * 		This Class
 * @param <E>
 * 		Entity Class
 *
 * @author Marc Magon
 */
public abstract class QueryBuilderCore<J extends QueryBuilderCore<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
		extends QueryBuilderExecutor<J, E, I>
{
	/**
	 * Where the "id" field is in
	 *
	 * @param id
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J find(I id)
	{
		Optional<Field> idField = GuiceContext.reflect().getFieldAnnotatedWithOfType(Id.class, id.getClass(), getEntityClass());
		if (!idField.isPresent())
		{
			Field[] fields = getEntityClass().getDeclaredFields();
			for (Field field : fields)
			{
				if (field.isAnnotationPresent(Id.class))
				{
					idField = Optional.of(field);
				}
			}
		}

		if (idField.isPresent())
		{
			getFilters().add(getRoot().get(idField.get().getName()).in(id));
		}
		else
		{
			getFilters().add(getRoot().get("id").in(id));
		}
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J inActiveRange()
	{
		List<ActiveFlag> flags = new ArrayList<>();
		for (ActiveFlag flag : ActiveFlag.values())
		{
			if (flag.ordinal() >= ActiveFlag.Active.ordinal())
			{
				flags.add(flag);
			}
		}
		getFilters().add(getRoot().get("activeFlag").in(flags));
		return (J) this;
	}

	public J inDateRange()
	{
		return inDateRange(LocalDateTime.now());
	}

	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime date)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get("effectiveFromDate"), date));
		getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get("effectiveToDate"), date));
		return (J) this;
	}

	@SuppressWarnings("unchecked")
	public J inVisibleRange()
	{
		List<ActiveFlag> flags = new ArrayList<>();
		for (ActiveFlag flag : ActiveFlag.values())
		{
			if (flag.ordinal() >= ActiveFlag.Invisible.ordinal())
			{
				flags.add(flag);
			}
		}
		getFilters().add(getRoot().get("activeFlag").in(flags));
		return (J) this;
	}

	public J inDateRangeSpecified(LocalDateTime fromDate)
	{
		return inDateRange(fromDate, LocalDateTime.now());
	}

	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime fromDate, LocalDateTime toDate)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get("effectiveFromDate"), fromDate));
		getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get("effectiveToDate"), toDate));
		return (J) this;
	}

}
