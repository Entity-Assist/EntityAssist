package com.jwebmp.entityassist.querybuilder;

import com.jwebmp.entityassist.SCDEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.jwebmp.entityassist.SCDEntity.*;
import static com.jwebmp.entityassist.enumerations.Operand.*;


public abstract class QueryBuilderSCD<J extends QueryBuilderSCD<J, E, I>, E extends SCDEntity<E, J, I>, I extends Serializable>
		extends QueryBuilder<J, E, I>
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
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange()
	{
		return inDateRange(LocalDateTime.now());
	}


	/**
	 * Returns the effective from and to date to be applied
	 *
	 * @param effectiveFromDate
	 * 		The date
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime effectiveFromDate)
	{
		where(getAttribute(EFFECTIVE_FROM_DATE_COLUMN_NAME), GreaterThanEqualTo, effectiveFromDate);
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), LessThanEqualTo, EndOfTime);

		return (J) this;
	}

	/**
	 * Returns the effective from and to date to be applied
	 *
	 * @param effectiveToDate
	 * 		The date
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime effectiveToDate, boolean toDate)
	{
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), LessThanEqualTo, effectiveToDate);
		return (J) this;
	}


	/**
	 * In date range from till now
	 *
	 * @param fromDate
	 * 		The date for from
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRangeSpecified(LocalDateTime fromDate)
	{
		return inDateRange(fromDate, LocalDateTime.now());
	}

	/**
	 * Specifies where effective from date greater and effective to date less than
	 *
	 * @param fromDate
	 * 		The from date
	 * @param toDate
	 * 		The to date
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime fromDate, LocalDateTime toDate)
	{
		where(getAttribute(EFFECTIVE_FROM_DATE_COLUMN_NAME), GreaterThanEqualTo, fromDate);
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), LessThanEqualTo, toDate);

		return (J) this;
	}

	@Override
	protected boolean onCreate(E entity)
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
			entity.setEffectiveToDate(EndOfTime);
		}
		return true;
	}

	/**
	 * Updates the on update to specify the new warehouse last updated
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return boolean
	 */
	@Override
	protected boolean onUpdate(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		return true;
	}
}
