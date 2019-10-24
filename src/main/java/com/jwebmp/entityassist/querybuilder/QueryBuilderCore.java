package com.guicedee.entityassist.querybuilder;

import com.guicedee.entityassist.SCDEntity;
import com.guicedee.entityassist.CoreEntity;
import com.guicedee.entityassist.enumerations.ActiveFlag;

import javax.persistence.metamodel.Attribute;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.guicedee.entityassist.enumerations.Operand.*;

/**
 * @param <J>
 * 		This Class
 * @param <E>
 * 		Entity Class
 *
 * @author GedMarc
 */
public abstract class QueryBuilderCore<J extends QueryBuilderCore<J, E, I>, E extends CoreEntity<E, J, I>, I extends Serializable>
		extends QueryBuilderSCD<J, E, I>
{
	/**
	 * The active flag column name
	 */
	@SuppressWarnings("WeakerAccess")
	public static final String ACTIVE_FLAG_DATE_COLUMN_NAME = "activeFlag";

	/**
	 * Filters from the Active Flag suite where it is in the active range
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J inActiveRange()
	{
		where((Attribute<Object, ActiveFlag>) getAttribute(ACTIVE_FLAG_DATE_COLUMN_NAME), InList, ActiveFlag.getActiveRangeAndUp());
		return (J) this;
	}

	/**
	 * Selects all records in the visible range
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J inVisibleRange()
	{
		where((Attribute<Object, ActiveFlag>) getAttribute(ACTIVE_FLAG_DATE_COLUMN_NAME), InList, ActiveFlag.getVisibleRangeAndUp());
		return (J) this;
	}

	/**
	 * Updates the current record with the given active flag type
	 * uses the merge
	 *
	 * @param newActiveFlagType
	 * 		The new flag type to apply
	 * @param entity
	 * 		The entity to operate on
	 *
	 * @return The entity
	 */
	public E delete(ActiveFlag newActiveFlagType, E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.now());
		entity.setActiveFlag(newActiveFlagType);
		getEntityManager().merge(entity);
		return entity;
	}

	/**
	 * Updates the current record with the given active flag type
	 *
	 * @param entity
	 * 		The entity to delete
	 *
	 * @return the entity
	 */
	@Override
	public E delete(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.now());
		entity.setActiveFlag(ActiveFlag.Deleted);
		getEntityManager().merge(entity);
		return entity;
	}

	/**
	 * Marks the record as archived updating the warehouse and effective to date timestamps
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return The Entity
	 */
	public E archive(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		entity.setEffectiveToDate(LocalDateTime.now());
		entity.setActiveFlag(ActiveFlag.Archived);

		getEntityManager().merge(entity);
		return entity;
	}

	/**
	 * Marks the given entity as the given status, with the effective to date and warehouse last updated as now
	 * Merges the entity, then detaches,
	 * <p>
	 * Persists the new record down with the end of time used
	 *
	 * @param entity
	 * 		The entity
	 * @param status
	 * 		The new status
	 *
	 * @return The updated entity
	 */
	@SuppressWarnings("unused")
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
		entity.setEffectiveToDate(SCDEntity.EndOfTime);
		entity.setActiveFlag(ActiveFlag.Active);

		persistNow(entity, true);

		return entity;
	}

}
