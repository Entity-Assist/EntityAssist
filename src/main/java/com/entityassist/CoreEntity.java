package com.entityassist;

import com.entityassist.enumerations.ActiveFlag;
import com.entityassist.querybuilder.QueryBuilderCore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
		extends SCDEntity<J, Q, I>
{
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
	 * A Row status identifier for a warehouse or OLAP system
	 */
	@Basic(optional = false,
			fetch = FetchType.LAZY)
	@Column(nullable = false,
			name = "ActiveFlag")
	@Enumerated(value = EnumType.STRING)
	private ActiveFlag activeFlag;

	/**
	 * Initialize the entity
	 */
	public CoreEntity()
	{
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
	 * Sets the JW ID to send if necessary
	 *
	 * @return any associated reference id
	 */
	@Transient
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
	@Transient
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
