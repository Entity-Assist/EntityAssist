package com.jwebmp.entityassist.querybuilder;

import com.jwebmp.entityassist.CoreEntity;
import com.jwebmp.entityassist.enumerations.ActiveFlag;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.jwebmp.entityassist.CoreEntity.*;

/**
 * @param <J>
 * 		This Class
 * @param <E>
 * 		Entity Class
 *
 * @author Marc Magon
 */
public abstract class QueryBuilderCore<J extends QueryBuilderCore<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
		extends QueryBuilder<J, E, I>
{
	private static final String EFFECTIVE_TO_DATE_COLUMN_NAME = "effectiveToDate";
	private static final String EFFECTIVE_FROM_DATE_COLUMN_NAME = "effectiveFromDate";
	private static final String ACTIVE_FLAG_DATE_COLUMN_NAME = "activeFlag";

	/**
	 * Filters from the Active Flag suite where it is in the active range
	 *
	 * @return
	 */
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
		getFilters().add(getCriteriaBuilder().in(getRoot().get(ACTIVE_FLAG_DATE_COLUMN_NAME))
		                                     .value(flags));
		return (J) this;
	}

	public J inDateRange()
	{
		return inDateRange(LocalDateTime.now());
	}

	public J inDateRange(LocalDateTime effectiveFromAndToDate)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get(EFFECTIVE_FROM_DATE_COLUMN_NAME), effectiveFromAndToDate));
		getFilters().add(getCriteriaBuilder().or(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get(EFFECTIVE_TO_DATE_COLUMN_NAME), effectiveFromAndToDate),
		                                         getCriteriaBuilder().equal(getRoot().get(EFFECTIVE_TO_DATE_COLUMN_NAME), EndOfTime)));
		return (J) this;
	}

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
		getFilters().add(getRoot().get(ACTIVE_FLAG_DATE_COLUMN_NAME)
		                          .in(flags));
		return (J) this;
	}

	public J inDateRangeSpecified(LocalDateTime fromDate)
	{
		return inDateRange(fromDate, LocalDateTime.now());
	}

	public J inDateRange(LocalDateTime fromDate, LocalDateTime toDate)
	{
		getFilters().add(getCriteriaBuilder().greaterThanOrEqualTo(getRoot().get(EFFECTIVE_FROM_DATE_COLUMN_NAME), fromDate));
		getFilters().add(getCriteriaBuilder().lessThanOrEqualTo(getRoot().get(EFFECTIVE_TO_DATE_COLUMN_NAME), toDate));
		return (J) this;
	}

	@Override
	protected boolean onUpdate(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		return true;
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
		entity.setEffectiveToDate(EndOfTime);
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
		entity.setActiveFlag(ActiveFlag.Archived);

		getEntityManager().merge(entity);
		getEntityManager().detach(entity);

		if (entity.builder()
		          .isIdGenerated())
		{
			entity.setId(null);
		}


		entity.setWarehouseCreatedTimestamp(LocalDateTime.now());
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveFromDate(LocalDateTime.now());
		entity.setEffectiveToDate(EndOfTime);
		entity.setActiveFlag(ActiveFlag.Deleted);
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
		entity.setEffectiveToDate(EndOfTime);
		entity.setActiveFlag(ActiveFlag.Active);
		persist(entity);

		return entity;
	}

	/**
	 * Marks the given entity as the given status, with the effective to date and warehouse last updated as now
	 * Merges the entity, then detaches,
	 * <p>
	 * Persists the new record down with the end of time used
	 *
	 * @param entity
	 * @param status
	 *
	 * @return
	 */
	public E closeAndReturnNewlyUpdate(E entity, ActiveFlag status)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.now());
		entity.setActiveFlag(status);

		getEntityManager().merge(entity);
		getEntityManager().detach(entity);

		entity.setId(null);
		entity.setWarehouseCreatedTimestamp(LocalDateTime.now());
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveFromDate(LocalDateTime.now());
		entity.setEffectiveToDate(EndOfTime);
		entity.setActiveFlag(ActiveFlag.Active);
		persist(entity);

		return entity;
	}

}
