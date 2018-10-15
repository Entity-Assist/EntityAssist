package com.jwebmp.entityassist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jwebmp.entityassist.converters.LocalDateTimeAttributeConverter;
import com.jwebmp.entityassist.enumerations.ActiveFlag;
import com.jwebmp.entityassist.querybuilder.QueryBuilderCore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

/**
 * @param <J>
 * 		Always this class (CRP)
 * @param <Q>
 * 		The associated query builder class
 *
 * @author GedMarc
 * @version 1.0
 * @since 06 Dec 2016
 */
@SuppressWarnings("unused")
@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
		getterVisibility = NONE,
		setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class CoreEntity<J extends CoreEntity<J, Q, I>, Q extends QueryBuilderCore<Q, J, I>, I extends Serializable>
		extends BaseEntity<J, Q, I>
{
	/**
	 * A timestamp designating the end of time or not applied
	 */
	public static final LocalDateTime EndOfTime = LocalDateTime.of(2999, 12, 31, 23, 59, 59, 999);
	/**
	 * Returns the date time formatter
	 */
	private static final DateTimeFormatter dateTimeOffsetFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	/**
	 * A reference ID for this entity - separate to ID
	 */
	@JsonProperty(value = "$jwid")
	@Transient
	private String referenceId;
	/**
	 * A date to designate when this record is effective from
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "EffectiveFromDate")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime effectiveFromDate;
	/**
	 * A date to designate when this record is effective to
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "EffectiveToDate")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime effectiveToDate;
	/**
	 * A date to mark when a warehouse can fetch the given record
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "WarehouseCreatedTimestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime warehouseCreatedTimestamp;
	/**
	 * A marker for the warehouse to identify when last this field was updated
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "WarehouseLastUpdatedTimestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime warehouseLastUpdatedTimestamp;
	/**
	 * A Row status identifier for a warehouse or OLAP system
	 */
	@Basic(optional = false,
			fetch = FetchType.EAGER)
	@Column(nullable = false,
			name = "ActiveFlag")
	@Enumerated(value = EnumType.STRING)
	private ActiveFlag activeFlag;

	/**
	 * Initialize the entity
	 */
	public CoreEntity()
	{
		effectiveToDate = EndOfTime;
		effectiveFromDate = LocalDateTime.now();
		warehouseCreatedTimestamp = LocalDateTime.now();
		warehouseLastUpdatedTimestamp = LocalDateTime.now();
		activeFlag = ActiveFlag.Active;
	}

	/**
	 * Constructs with no parameters set, Great for search criteria
	 *
	 * @param blank
	 * 		constructs with nothing
	 */
	public CoreEntity(@SuppressWarnings("unused") boolean blank)
	{
		//No Config
	}

	/**
	 * Returns the effective from date for the given setting
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	public LocalDateTime getEffectiveFromDate()
	{
		return effectiveFromDate;
	}

	/**
	 * Sets the effective from date value for default value
	 *
	 * @param effectiveFromDate
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("all")
	public J setEffectiveFromDate(@NotNull LocalDateTime effectiveFromDate)
	{
		this.effectiveFromDate = effectiveFromDate;
		return (J) this;
	}

	/**
	 * Returns the effice to date setting for active flag calculation
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	public LocalDateTime getEffectiveToDate()
	{
		return effectiveToDate;
	}

	/**
	 * Sets the effective to date column value for active flag determination
	 *
	 * @param effectiveToDate
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("all")
	public J setEffectiveToDate(@NotNull LocalDateTime effectiveToDate)
	{
		this.effectiveToDate = effectiveToDate;
		return (J) this;
	}

	/**
	 * Returns the active flag
	 *
	 * @return The associated active flag
	 */
	public ActiveFlag getActiveFlag()
	{
		return activeFlag;
	}

	/**
	 * Sets the active flag
	 *
	 * @param activeFlag
	 * 		The active flag
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setActiveFlag(ActiveFlag activeFlag)
	{
		this.activeFlag = activeFlag;
		return (J) this;
	}

	/**
	 * Finds the entity with the given ID
	 *
	 * @param id
	 * 		The id to look for
	 *
	 * @return If it is found through a get method
	 */
	public Optional<J> find(I id)
	{
		return builder().find(id)
		                .get();
	}

	/**
	 * Finds all the entity types
	 *
	 * @return A list of get all from the current builder
	 */
	public List<J> findAll()
	{
		return builder().getAll();
	}

	/**
	 * Returns the warehouse created timestamp column value
	 *
	 * @return The current time
	 */
	public LocalDateTime getWarehouseCreatedTimestamp()
	{
		return warehouseCreatedTimestamp;
	}

	/**
	 * Sets the warehouse created timestamp
	 *
	 * @param warehouseCreatedTimestamp
	 * 		The time to apply
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("all")
	public J setWarehouseCreatedTimestamp(@NotNull LocalDateTime warehouseCreatedTimestamp)
	{
		this.warehouseCreatedTimestamp = warehouseCreatedTimestamp;
		return (J) this;
	}

	/**
	 * Deletes this entity with the entity mananger. This will remove the row.
	 *
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J delete()
	{
		((QueryBuilderCore) builder())
				.delete(this);
		return (J) this;
	}

	/**
	 * Returns the last time the warehouse timestamp column was updated
	 *
	 * @return The time
	 */
	@SuppressWarnings("all")
	public LocalDateTime getWarehouseLastUpdatedTimestamp()
	{
		return warehouseLastUpdatedTimestamp;
	}

	/**
	 * Sets the last time the warehouse timestamp column was updated
	 *
	 * @param warehouseLastUpdatedTimestamp
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings("all")
	public J setWarehouseLastUpdatedTimestamp(@NotNull LocalDateTime warehouseLastUpdatedTimestamp)
	{
		this.warehouseLastUpdatedTimestamp = warehouseLastUpdatedTimestamp;
		return (J) this;
	}

	/**
	 * Sets the JW ID to send if necessary
	 *
	 * @return any associated reference id
	 */
	public String getReferenceId()
	{
		return referenceId;
	}

	/**
	 * Sets the JW ID to send if necessary
	 *
	 * @param referenceId
	 * 		a transient identifier
	 */
	@NotNull
	@SuppressWarnings("all")
	public J setReferenceId(@NotNull String referenceId)
	{
		this.referenceId = referenceId;
		return (J) this;
	}

	/**
	 * Returns the formatter for date time offset (sql server)
	 *
	 * @return the formatter used for UTC
	 */
	@NotNull
	protected DateTimeFormatter getDateTimeOffsetFormatter()
	{
		return dateTimeOffsetFormatter;
	}
}
