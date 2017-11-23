package za.co.mmagon.entityassist;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import za.co.mmagon.entityassist.converters.LocalDateTimeAttributeConverter;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class CoreEntity<J extends CoreEntity<J, Q, I>, Q extends QueryBuilderCore<Q, J, I>, I extends Serializable>
		extends BaseEntity<J, Q, I>
		implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the date time formatter
	 */
	private static final transient DateTimeFormatter dateTimeOffsetFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@JsonProperty(value = "$jwid")
	@Transient
	private String referenceId;
	@Basic(optional = false, fetch = FetchType.LAZY)
	@NotNull
	@Column(nullable = false, name = "EffectiveFromDate", columnDefinition = "datetime")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime effectiveFromDate;
	@Basic(optional = false, fetch = FetchType.LAZY)
	@NotNull
	@Column(nullable = false, name = "EffectiveToDate", columnDefinition = "datetime")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime effectiveToDate;
	@Basic(optional = false, fetch = FetchType.LAZY)
	@NotNull
	@Column(nullable = false, name = "WarehouseCreatedTimestamp", columnDefinition = "datetime")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime warehouseCreatedTimestamp;
	@Basic(optional = false, fetch = FetchType.LAZY)
	@NotNull
	@Column(nullable = false, name = "WarehouseLastUpdatedTimestamp", columnDefinition = "datetime")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime warehouseLastUpdatedTimestamp;
	@Basic(optional = false, fetch = FetchType.EAGER)
	@Column(nullable = false, name = "ActiveFlag", columnDefinition = "varchar(max)")
	@Enumerated(value = EnumType.STRING)
	@NotNull
	private ActiveFlag activeFlag;


	/**
	 * Initialize the entity
	 */
	public CoreEntity()
	{
		effectiveToDate = LocalDateTime.of(2999, 12, 31, 23, 59, 59, 999);
		effectiveFromDate = LocalDateTime.now();
		warehouseCreatedTimestamp = LocalDateTime.now();
		warehouseLastUpdatedTimestamp = LocalDateTime.now();
		activeFlag = ActiveFlag.Active;
	}

	/**
	 * Returns the effective from date for the given setting
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	protected LocalDateTime getEffectiveFromDate()
	{
		return effectiveFromDate;
	}

	/**
	 * Performs the onCreate method setting the effective to date if it wasn't null, the effective from date if it wasn't set and the active flag derived
	 */
	@PrePersist
	public void onCreate()
	{
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
	protected J setEffectiveFromDate(@NotNull LocalDateTime effectiveFromDate)
	{
		this.effectiveFromDate = effectiveFromDate;
		return (J) this;
	}

	/**
	 * Returns the effice to date setting for active flag calculation
	 *
	 * @return
	 */
	@Nullable
	@SuppressWarnings("all")
	protected LocalDateTime getEffectiveToDate()
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
	protected J setEffectiveToDate(@NotNull LocalDateTime effectiveToDate)
	{
		this.effectiveToDate = effectiveToDate;
		return (J) this;
	}

	/**
	 * Performs the update command for entities
	 */
	@PreUpdate
	public void onUpdate()
	{
		setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
	}

	/**
	 * Returns the active flag
	 *
	 * @return
	 */
	protected ActiveFlag getActiveFlag()
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
	protected J setActiveFlag(ActiveFlag activeFlag)
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
		return builder().find(id).get();
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
	@Nullable
	protected LocalDateTime getWarehouseCreatedTimestamp()
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
	protected J setWarehouseCreatedTimestamp(@NotNull LocalDateTime warehouseCreatedTimestamp)
	{
		this.warehouseCreatedTimestamp = warehouseCreatedTimestamp;
		return (J) this;
	}

	/**
	 * Returns the last time the warehouse timestamp column was updated
	 *
	 * @return
	 */
	@Nullable
	@SuppressWarnings("all")
	protected LocalDateTime getWarehouseLastUpdatedTimestamp()
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
	protected J setWarehouseLastUpdatedTimestamp(@NotNull LocalDateTime warehouseLastUpdatedTimestamp)
	{
		this.warehouseLastUpdatedTimestamp = warehouseLastUpdatedTimestamp;
		return (J) this;
	}

	/**
	 * Sets the JW ID to send if necessary
	 *
	 * @return
	 */
	@Nullable
	protected String getReferenceId()
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
	protected J setReferenceId(@NotNull String referenceId)
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
