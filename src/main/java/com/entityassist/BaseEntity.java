package com.entityassist;

import com.entityassist.querybuilder.EntityAssistStrings;
import com.entityassist.querybuilder.QueryBuilder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.guicedee.guicedinjection.GuiceContext;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
		getterVisibility = NONE,
		setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class BaseEntity<J extends BaseEntity<J, Q, I>, Q extends QueryBuilder<Q, J, I>, I extends Serializable>
{
	private static final Logger log = Logger.getLogger(BaseEntity.class.getName());

	@Transient
	@JsonIgnore
	private Map<Serializable, Object> properties;

	/**
	 * Constructs a new base entity type
	 */
	public BaseEntity()
	{
		//No configuration needed
		setFake(true);
	}

	/**
	 * Returns the id of the given type in the generic decleration
	 *
	 * @return Returns the ID
	 */
	@NotNull
	public abstract I getId();

	/**
	 * Returns the id of the given type in the generic decleration
	 *
	 * @param id
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	@NotNull
	public abstract J setId(I id);

	/**
	 * Persists this object through the builder
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J persist()
	{
		builder().persist((J) this);
		return (J) this;
	}

	/**
	 * Returns the builder associated with this entity
	 *
	 * @return
	 */
	@SuppressWarnings({"unchecked", "notnull"})
	@NotNull
	public Q builder()
	{
		Class<Q> foundQueryBuilderClass = getClassQueryBuilderClass();
		Q instance = null;
		try
		{
			instance = GuiceContext.get(foundQueryBuilderClass);
			instance.setEntity(this);
			return instance;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to instantiate the query builder class. Make sure there is a blank constructor", e);
			throw new EntityAssistException("Unable to construct builder", e);
		}
	}

	/**
	 * Returns this classes associated query builder class
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	protected Class<Q> getClassQueryBuilderClass()
	{
		return (Class<Q>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	/**
	 * Deletes this entity with the entity mananger. This will remove the row.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J delete()
	{
		((QueryBuilder) builder())
				.delete(this);
		return (J) this;
	}

	/**
	 * Updates this object through the builder
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J update()
	{
		builder().update((J) this);
		return (J) this;
	}

	/**
	 * Persists this object through the builder
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J persistNow()
	{
		builder().persistNow((J) this);
		return (J) this;
	}

	/**
	 * Validates this entity according to any validation rules
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J validate()
	{
		builder().validateEntity((J) this);
		return (J) this;
	}

	/**
	 * Returns if this entity is operating as a fake or not (testing or dto)
	 *
	 * @return
	 */
	@NotNull
	public boolean isFake()
	{
		return getProperties().containsKey(EntityAssistStrings.FAKE_KEY) && Boolean.parseBoolean(getProperties().get(EntityAssistStrings.FAKE_KEY)
		                                                                                                        .toString());
	}

	/**
	 * Any DB Transient Maps
	 * <p>
	 * Sets any custom properties for this core entity.
	 * Dto Read only structure. Not for storage unless mapped as such in a sub-method
	 *
	 * @return
	 */
	@NotNull
	public Map<Serializable, Object> getProperties()
	{
		if (properties == null)
		{
			properties = new HashMap<>();
		}
		return properties;
	}

	/**
	 * Sets any custom properties for this core entity.
	 * Dto Read only structure. Not for storage unless mapped as such in a sub-method
	 *
	 * @param properties
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J setProperties(@NotNull Map<Serializable, Object> properties)
	{
		this.properties = properties;
		return (J) this;
	}

	/**
	 * Sets the fake property
	 *
	 * @param fake
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFake(boolean fake)
	{
		if (fake)
		{
			getProperties().put(EntityAssistStrings.FAKE_KEY, true);
		}
		else
		{
			getProperties().remove(EntityAssistStrings.FAKE_KEY);
		}
		return (J) this;
	}

	/**
	 * Returns this classes associated id class type
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public Class<I> getClassIDType()
	{
		return (Class<I>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	}
}
