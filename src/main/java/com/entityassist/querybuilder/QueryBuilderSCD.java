package com.entityassist.querybuilder;

import com.entityassist.*;
import com.entityassist.enumerations.Operand;
import com.guicedee.logger.LogFactory;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.*;
import java.util.logging.Level;

import static com.entityassist.SCDEntity.*;
import static java.time.ZoneOffset.*;

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
	 * The effective from date column name
	 */
	@SuppressWarnings("WeakerAccess")
	public static final String WAREHOUSE_CREATED_DATE_COLUMN_NAME = "warehouseCreatedTimestamp";
	/**
	 * The effective from date column name
	 */
	@SuppressWarnings("WeakerAccess")
	public static final String WAREHOUSE_UPDATED_DATE_COLUMN_NAME = "warehouseLastUpdatedTimestamp";
	
	/**
	 * Where effective from date is greater than today
	 *
	 * @return This
	 */
	@Override
	@NotNull
	public J inDateRange()
	{
		return inDateRange(getNow());
	}
	
	
	/**
	 * Returns the effective from and to date to be applied
	 * <p>
	 * Usually getDate()
	 *
	 * @param betweenThisDate The date
	 *
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime betweenThisDate)
	{
		where(getAttribute(EFFECTIVE_FROM_DATE_COLUMN_NAME), Operand.LessThanEqualTo, convertToUTCDateTime(betweenThisDate));
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), Operand.GreaterThanEqualTo, convertToUTCDateTime(betweenThisDate));
		return (J) this;
	}
	
	/**
	 * Returns the effective from and to date to be applied when only the effective date is taken into consideration
	 *
	 * @param effectiveToDate The date
	 *
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime effectiveToDate, boolean toDate)
	{
		where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), Operand.LessThanEqualTo, convertToUTCDateTime(effectiveToDate));
		return (J) this;
	}
	
	
	/**
	 * In date range from till now
	 *
	 * @param fromDate The date for from
	 *
	 * @return This
	 */
	@Override
	@NotNull
	public J inDateRangeSpecified(LocalDateTime fromDate)
	{
		return inDateRange(fromDate, RootEntity.getNow());
	}
	
	/**
	 * Specifies where effective from date greater and effective to date less than
	 *
	 * @param fromDate The from date
	 * @param toDate   The to date
	 *
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public J inDateRange(LocalDateTime fromDate, LocalDateTime toDate)
	{
		if (fromDate != null)
		{
			where(getAttribute(EFFECTIVE_FROM_DATE_COLUMN_NAME), Operand.GreaterThanEqualTo, convertToUTCDateTime(fromDate));
		}
		//noinspection ReplaceNullCheck
		if (toDate != null)
		{
			where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), Operand.LessThanEqualTo, convertToUTCDateTime(toDate));
		}
		else
		{
			where(getAttribute(EFFECTIVE_TO_DATE_COLUMN_NAME), Operand.LessThanEqualTo, EndOfTime.atOffset(UTC));
		}
		
		return (J) this;
	}
	
	public J withWarehouseCreated(LocalDateTime time)
	{
		if (time != null)
		{
			where(getAttribute(WAREHOUSE_CREATED_DATE_COLUMN_NAME), Operand.Equals, convertToUTCDateTime(time));
		}
		//noinspection unchecked
		return (J) this;
	}
	
	public J withWarehouseUpdated(LocalDateTime time)
	{
		if (time != null)
		{
			where(getAttribute(WAREHOUSE_UPDATED_DATE_COLUMN_NAME), Operand.Equals, convertToUTCDateTime(time));
		}
		//noinspection unchecked
		return (J) this;
	}
	
	@Override
	public @NotNull E update(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(convertToUTCDateTime(RootEntity.getNow()));
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
	
	public @NotNull E update(E entity, java.time.Duration expiresIn)
	{
		entity.setEffectiveToDate(convertToUTCDateTime(RootEntity.getNow())
		                                               .plus(expiresIn));
		entity.setWarehouseLastUpdatedTimestamp(convertToUTCDateTime(RootEntity.getNow()));
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
	 *
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
	 *
	 * @return true if must create
	 */
	@Override
	public boolean onCreate(E entity)
	{
		if (entity.getWarehouseCreatedTimestamp() == null)
		{
			entity.setWarehouseCreatedTimestamp(convertToUTCDateTime(RootEntity.getNow()));
		}
		if (entity.getWarehouseLastUpdatedTimestamp() == null)
		{
			entity.setWarehouseLastUpdatedTimestamp(convertToUTCDateTime(RootEntity.getNow()));
		}
		if (entity.getEffectiveFromDate() == null)
		{
			entity.setEffectiveFromDate(convertToUTCDateTime(RootEntity.getNow()));
		}
		if (entity.getEffectiveToDate() == null)
		{
			entity.setEffectiveToDate(EndOfTime.atOffset(UTC));
		}
		return true;
	}
	
	/**
	 * Updates the on update to specify the new warehouse last updated
	 *
	 * @param entity The entity
	 *
	 * @return boolean
	 */
	@Override
	public boolean onUpdate(E entity)
	{
		entity.setWarehouseLastUpdatedTimestamp(convertToUTCDateTime(RootEntity.getNow()));
		return true;
	}
	
	public static OffsetDateTime convertToUTCDateTime(LocalDateTime ldt) {
		if (ldt == null)
		{
			return null;
		}
		ZonedDateTime zonedDateTime = ldt.atZone(ZoneId.systemDefault());
		ZonedDateTime utcZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
		OffsetDateTime offsetDateTime = utcZonedDateTime.toOffsetDateTime();
		return offsetDateTime;
	}
	
	public static LocalDateTime convertToLocalDateTime(OffsetDateTime ldt) {
		if (ldt == null)
		{
			return null;
		}
		ZonedDateTime zonedDateTime = ldt.atZoneSameInstant(ZoneId.systemDefault());
		return zonedDateTime.toLocalDateTime();
	}
	
	public static LocalDateTime convertToLocalDateTime(OffsetDateTime ldt, ZoneId zone) {
		if (ldt == null)
		{
			return null;
		}
		ZonedDateTime zonedDateTime = ldt.atZoneSameInstant(zone);
		return zonedDateTime.toLocalDateTime();
	}
	
	public static LocalDateTime convertToLocalDateTime(OffsetDateTime ldt, String timezone) {
		if (ldt == null)
		{
			return null;
		}
		ZonedDateTime zonedDateTime = ldt.atZoneSameInstant(ZoneId.of(timezone));
		return zonedDateTime.toLocalDateTime();
	}
	
}
