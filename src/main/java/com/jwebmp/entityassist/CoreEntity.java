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
@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
		getterVisibility = NONE,
		setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class CoreEntity<J extends CoreEntity<J, Q, I>, Q extends QueryBuilderCore<Q, J, I>, I extends Serializable>
		extends BaseEntity<J, Q, I>
		implements Serializable
{
	public static final LocalDateTime EndOfTime = LocalDateTime.of(2999, 12, 31, 23, 59, 59, 999);
	private static final long serialVersionUID = 1L;
	/**
	 * Returns the date time formatter
	 */
	private static final transient DateTimeFormatter dateTimeOffsetFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	@JsonProperty(value = "$jwid")
	@Transient
	private String referenceId;
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "EffectiveFromDate")
	@Convert(converter = LocalDateTimeAttributeConverter.class)

	private LocalDateTime effectiveFromDate;
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "EffectiveToDate")
	@Convert(converter = LocalDateTimeAttributeConverter.class)

	private LocalDateTime effectiveToDate;
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "WarehouseCreatedTimestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime warehouseCreatedTimestamp;
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "WarehouseLastUpdatedTimestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime warehouseLastUpdatedTimestamp;
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
	 */
	public CoreEntity(boolean blank)
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
	 * @return
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
	 * @return
	 */
	public ActiveFlag getActiveFlag()
	{
		return activeFlag;
	}

	/**
	 * Sets the active flag
	 *
	 * @param activeFlag
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setActiveFlag(ActiveFlag activeFlag)
	{
		this.activeFlag = activeFlag;
		return (J) this;
	}

	/**
	 * Finds the entity with the given ID
	 *
	 * @param id
	 *
	 * @return
	 */
	public Optional<J> find(I id)
	{
		return builder().find(id)
		                .get();
	}

	/**
	 * Finds all the entity types
	 *
	 * @return
	 */
	public List<J> findAll()
	{
		return builder().getAll();
	}

	/**
	 * Returns the warehouse created timestamp column value
	 *
	 * @return
	 */
	public LocalDateTime getWarehouseCreatedTimestamp()
	{
		return warehouseCreatedTimestamp;
	}

	/**
	 * Sets the warehouse created timestamp
	 *
	 * @param warehouseCreatedTimestamp
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("all")
	public J setWarehouseCreatedTimestamp(@NotNull LocalDateTime warehouseCreatedTimestamp)
	{
		this.warehouseCreatedTimestamp = warehouseCreatedTimestamp;
		return (J) this;
	}

	/**
	 * Returns the last time the warehouse timestamp column was updated
	 *
	 * @return
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
	 * @return
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
	 * @return
	 */
	public String getReferenceId()
	{
		return referenceId;
	}

	/**
	 * Sets the JW ID to send if necessary
	 *
	 * @param referenceId
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
	 * @return
	 */
	@NotNull
	protected DateTimeFormatter getDateTimeOffsetFormatter()
	{
		return dateTimeOffsetFormatter;
	}
}
