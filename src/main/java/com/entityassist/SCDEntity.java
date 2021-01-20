package com.entityassist;

import com.entityassist.converters.LocalDateTimeAttributeConverter;
import com.entityassist.querybuilder.QueryBuilderSCD;
import com.guicedee.guicedinjection.json.LocalDateTimeDeserializer;
import com.guicedee.guicedinjection.json.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
		getterVisibility = NONE,
		setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class SCDEntity<J extends SCDEntity<J, Q, I>, Q extends QueryBuilderSCD<Q, J, I>, I extends Serializable>
		extends BaseEntity<J, Q, I>
{
	/**
	 * A timestamp designating the end of time or not applied
	 */
	public static LocalDateTime StartOfTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
	public static final LocalDateTime EndOfTime = LocalDateTime.of(2999, 12, 31, 23, 59, 59, 999);

	/**
	 * A date to designate when this record is effective from
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "EffectiveFromDate")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime effectiveFromDate;
	/**
	 * A date to designate when this record is effective to
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "EffectiveToDate")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime effectiveToDate;
	/**
	 * A date to mark when a warehouse can fetch the given record
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "WarehouseCreatedTimestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime warehouseCreatedTimestamp;
	/**
	 * A marker for the warehouse to identify when last this field was updated
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "WarehouseLastUpdatedTimestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime warehouseLastUpdatedTimestamp;

	public SCDEntity()
	{
		effectiveToDate = EndOfTime;
		effectiveFromDate = LocalDateTime.now();
		warehouseCreatedTimestamp = LocalDateTime.now();
		warehouseLastUpdatedTimestamp = LocalDateTime.now();
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
	 * Returns the last time the warehouse timestamp column was updated
	 *
	 * @return The time
	 */
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
	 * Deletes this entity with the entity mananger. This will remove the row.
	 *
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J delete()
	{
		((QueryBuilderSCD) builder())
				.delete(this);
		return (J) this;
	}
}
