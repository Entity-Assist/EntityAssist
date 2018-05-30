package za.co.mmagon.entityassist.querybuilder;

import za.co.mmagon.entityassist.CoreEntity;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.querybuilder.builders.QueryBuilderExecutor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static za.co.mmagon.entityassist.CoreEntity.EndOfTime;

/**
 * @param <J>
 * 		This Class
 * @param <E>
 * 		Entity Class
 *
 * @author Marc Magon
 */
public abstract class QueryBuilderCore<J extends QueryBuilderCore<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
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
		getFilters().add(getCriteriaBuilder().in(getRoot().get("activeFlag"))
		                                     .value(flags));
		return (J) this;
	}

	public J inDateRange()
	{
		return inDateRange(LocalDateTime.now());
	}

	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime effectiveFromAndToDate)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get("effectiveFromDate"), effectiveFromAndToDate));
		getFilters().add(getCriteriaBuilder().or(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get("effectiveToDate"), effectiveFromAndToDate),
		                                         getCriteriaBuilder().equal(getRoot().get("effectiveToDate"), EndOfTime)));
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
		getFilters().add(getRoot().get("activeFlag")
		                          .in(flags));
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

	@Override
	protected void onUpdate(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
	}

	/**
	 * Updates the current record with the given active flag type
	 *
	 * @param newActiveFlagType
	 * @param entity
	 *
	 * @return
	 */
	public E delete(ActiveFlag newActiveFlagType, E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.now());
		entity.setActiveFlag(newActiveFlagType);
		getEntityManager().merge(entity);
		getEntityManager().detach(entity);

		entity.setId(null);
		entity.setWarehouseCreatedTimestamp(LocalDateTime.now());
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveFromDate(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.of(2999, 12, 31, 11, 59, 59, 999));
		entity.setActiveFlag(ActiveFlag.Active);
		persist(entity);

		return entity;
	}

	/**
	 * Updates the current record with the given active flag type
	 *
	 * @param entity
	 *
	 * @return
	 */
	@Override
	public E delete(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.now());
		entity.setActiveFlag(ActiveFlag.Deleted);

		getEntityManager().merge(entity);
		getEntityManager().detach(entity);

		entity.setId(null);
		entity.setWarehouseCreatedTimestamp(LocalDateTime.now());
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveFromDate(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.of(2999, 12, 31, 11, 59, 59, 999));
		entity.setActiveFlag(ActiveFlag.Active);
		persist(entity);

		return entity;
	}

	public E archive(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.now());
		entity.setActiveFlag(ActiveFlag.Archived);

		getEntityManager().merge(entity);
		getEntityManager().detach(entity);

		entity.setId(null);
		entity.setWarehouseCreatedTimestamp(LocalDateTime.now());
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveFromDate(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.of(2999, 12, 31, 11, 59, 59, 999));
		entity.setActiveFlag(ActiveFlag.Active);
		persist(entity);

		return entity;
	}

}
