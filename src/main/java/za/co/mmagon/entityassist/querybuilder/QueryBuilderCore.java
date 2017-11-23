package za.co.mmagon.entityassist.querybuilder;

import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.querybuilder.builders.QueryBuilderExecutor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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


	@SuppressWarnings("unchecked")
	public J inActiveRange()
	{
		Set<ActiveFlag> flags = new LinkedHashSet<>();
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
