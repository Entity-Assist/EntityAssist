package com.entityassist.querybuilder;

import com.entityassist.SCDEntity;
import com.entityassist.enumerations.Operand;
import com.guicedee.logger.LogFactory;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;

@SuppressWarnings("unused")
public abstract class QueryBuilderSCD<J extends QueryBuilderSCD<J, E, I>, E extends SCDEntity<E, J, I>, I extends Serializable>
		extends QueryBuilder<J, E, I>
		implements com.entityassist.services.querybuilders.IQueryBuilderSCD<J, E, I>
{
	/**
	 * The effective to date column name
	 */
	@SuppressWarnings("WeakerAccess")
	public static final String EFFECTIVE_TO_DATE_COLUMN_NAME = "effectiveToDate";
	/**
	 * The effective from date column name
	 */
	@SuppressWarnings("WeakerAccess")
	public static final String EFFECTIVE_FROM_DATE_COLUMN_NAME = "effectiveFromDate";
	
	
	/**
	 * Where effective from date is greater than today
	 *
	 * @return This
	 */
	@Override
	@NotNull
	public J inDateRange()
	{
		return inDateRange(LocalDateTime.now());
	}
	
	
	/**
	 * Returns the effective from and to date to be applied
	 * <p>
	 * Usually getDate()
	 *
	 * @param betweenThisDate The date
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime betweenThisDate)
	{
		where(getAttribute(EFFECTIVE_FROM_DATE_COLUMN_NAME), Operand.LessThanEqualTo, betweenThisDate);
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), Operand.GreaterThanEqualTo, betweenThisDate);
		return (J) this;
	}
	
	/**
	 * Returns the effective from and to date to be applied when only the effective date is taken into consideration
	 *
	 * @param effectiveToDate The date
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime effectiveToDate, boolean toDate)
	{
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), Operand.LessThanEqualTo, effectiveToDate);
		return (J) this;
	}
	
	
	/**
	 * In date range from till now
	 *
	 * @param fromDate The date for from
	 * @return This
	 */
	@Override
	@NotNull
	public J inDateRangeSpecified(LocalDateTime fromDate)
	{
		return inDateRange(fromDate, LocalDateTime.now());
	}
	
	/**
	 * Specifies where effective from date greater and effective to date less than
	 *
	 * @param fromDate The from date
	 * @param toDate   The to date
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime fromDate, LocalDateTime toDate)
	{
		where(getAttribute(EFFECTIVE_FROM_DATE_COLUMN_NAME), Operand.GreaterThanEqualTo, fromDate);
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), Operand.LessThanEqualTo, toDate);
		
		return (J) this;
	}
	
	@Override
	public @NotNull E update(E entity)
	{
		E originalEntity = entity.builder()
		                         .find(entity.getId())
		                         .get()
		                         .orElseThrow();
		originalEntity.setEffectiveToDate(LocalDateTime.now());
		originalEntity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		try
		{
			return super.update(entity);
		}
		catch (SQLException e)
		{
			LogFactory.getLog("QueryBuilderSCD")
			          .log(Level.WARNING, "Unable to update id : " + e, e);
			return entity;
		}
	}
	
	public @NotNull E update(E entity,java.time.Duration expiresIn)
	{
		E originalEntity = entity.builder()
		                         .find(entity.getId())
		                         .get()
		                         .orElseThrow(()-> new IllegalArgumentException("Entity not found"));
		originalEntity.setEffectiveToDate(LocalDateTime.now().plus(expiresIn));
		originalEntity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		try
		{
			return super.update(entity);
		}
		catch (SQLException e)
		{
			LogFactory.getLog("QueryBuilderSCD")
			          .log(Level.WARNING, "Unable to update id : " + e, e);
			return entity;
		}
	}
	
	/**
	 * Performs any required logic between the original and new entities during an update operation
	 * which is a delete and marking of the record as historical, and the insert of a new record which is updated
	 * <p>
	 * The old and new entities may have the same id, the new entity id is emptied after this call for persistence.
	 *
	 * @param originalEntity The entity that is going to be deleted
	 * @param newEntity      The entity that is going to be created
	 * @return currently always true @TODO
	 */
	@Override
	public boolean onDeleteUpdate(E originalEntity, E newEntity)
	{
		return true;
	}
	
	/**
	 * Sets the SCD values to new ones if not present
	 *
	 * @param entity The entity
	 * @return true if must create
	 */
	@Override
	public boolean onCreate(E entity)
	{
		if (entity.getWarehouseCreatedTimestamp() == null)
		{
			entity.setWarehouseCreatedTimestamp(LocalDateTime.now());
		}
		if (entity.getWarehouseLastUpdatedTimestamp() == null)
		{
			entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		}
		if (entity.getEffectiveFromDate() == null)
		{
			entity.setEffectiveFromDate(LocalDateTime.now());
		}
		if (entity.getEffectiveToDate() == null)
		{
			entity.setEffectiveToDate(SCDEntity.EndOfTime);
		}
		return true;
	}
	
	/**
	 * Updates the on update to specify the new warehouse last updated
	 *
	 * @param entity The entity
	 * @return boolean
	 */
	@Override
	public boolean onUpdate(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		return true;
	}
}
