package com.entityassist;

import com.entityassist.querybuilder.builders.*;
import com.entityassist.services.entities.*;
import com.fasterxml.jackson.annotation.*;
import com.guicedee.guicedinjection.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.*;
import java.lang.reflect.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@SuppressWarnings("unused")
@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
                getterVisibility = NONE,
                setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class RootEntity<J extends RootEntity<J, Q, I>, Q extends QueryBuilderRoot<Q, J, I>, I extends Serializable>
		implements IRootEntity<J, Q, I>
{
	private static final Logger log = Logger.getLogger(RootEntity.class.getName());
	
	private static final ThreadLocal<LocalDateTime> now = new ThreadLocal<>();
	
	public static LocalDateTime getNow()
	{
		LocalDateTime value = now.get();
		if (value == null)
		{
			return LocalDateTime.now();
		}
		return value;
	}
	
	public static void setNow(LocalDateTime now)
	{
		RootEntity.now.set(now);
	}
	
	public static void tick()
	{
		if (now.get() != null)
		{
			now.set(now.get()
			           .plusSeconds(1L));
		}
	}
	
	public static void tickMilli()
	{
		if (now.get() != null)
		{
			now.set(now.get()
			           .plusNanos(100L));
		}
	}
	
	@Transient
	@JsonIgnore
	private Map<Serializable, Object> properties;
	
	/**
	 * Constructs a new base entity type
	 */
	public RootEntity()
	{
		//No configuration needed
		setFake(true);
	}
	
	/**
	 * Returns the builder associated with this entity
	 *
	 * @return The associated builder
	 */
	@SuppressWarnings({"notnull"})
	@NotNull
	public Q builder()
	{
		Class<Q> foundQueryBuilderClass = getClassQueryBuilderClass();
		Q instance = null;
		try
		{
			instance = GuiceContext.get(foundQueryBuilderClass);
			//noinspection unchecked
			instance.setEntity((J) this);
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
	 * @return The query builder identified class
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	protected Class<Q> getClassQueryBuilderClass()
	{
		return (Class<Q>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}
	
	/**
	 * Validates this entity according to any validation rules
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public List<String> validate()
	{
		return builder().validateEntity((J) this);
	}
	
	/**
	 * Returns if this entity is operating as a fake or not (testing or dto)
	 *
	 * @return
	 */
	@NotNull
	public boolean isFake()
	{
		return getProperties().containsKey("fake") && Boolean.parseBoolean(getProperties().get("fake")
		                                                                                    .toString());
	}
	
	/**
	 * Sets the fake property
	 *
	 * @param fake
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFake(boolean fake)
	{
		if (fake)
		{
			getProperties().put("fake", true);
		}
		else
		{
			getProperties().remove("fake");
		}
		return (J) this;
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
