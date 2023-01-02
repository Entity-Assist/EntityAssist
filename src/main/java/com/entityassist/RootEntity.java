package com.entityassist;

import com.entityassist.exceptions.*;
import com.entityassist.querybuilder.builders.*;
import com.entityassist.services.entities.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.*;
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
	@JsonIgnore
	private static final Logger log = Logger.getLogger(RootEntity.class.getName());
	
	@JsonIgnore
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
	
	/**
	 * Constructs a new base entity type
	 */
	public RootEntity()
	{
		//No configuration needed
	}
	
	/**
	 * Returns the builder associated with this entity
	 *
	 * @return The associated builder
	 */
	@SuppressWarnings({"notnull", "unchecked"})
	@NotNull
	public Q builder(EntityManager entityManager)
	{
		Class<Q> foundQueryBuilderClass = getClassQueryBuilderClass();
		Q instance = null;
		try
		{
			instance = foundQueryBuilderClass.getDeclaredConstructor().newInstance();
			instance.setEntityManager(entityManager);
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
		List<String> errors = new ArrayList<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<?> constraintViolations = validator.validate(this);
		if (!constraintViolations.isEmpty())
		{
			for (Object constraintViolation : constraintViolations)
			{
				ConstraintViolation<?> contraints = (ConstraintViolation<?>) constraintViolation;
				String error = contraints
						               .getRootBeanClass()
						               .getSimpleName() + "." + contraints.getPropertyPath() + " " + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}
	
	@Override
	public String getTableName()
	{
		Class<?> c = getClass();
		Table t = c.getAnnotation(Table.class);
		String tableName = "";
		if (t != null)
		{
			String catalog = t.catalog();
			if (!catalog.isEmpty())
			{
				tableName += catalog + ".";
			}
			String schema = t.schema();
			if (!schema.isEmpty())
			{
				tableName += schema + ".";
			}
			if (t.name() == null || t.name().isEmpty() || t.name().isBlank())
			{
				tableName += c.getSimpleName();
			}
			else
			{ tableName += t.name(); }
		}
		if (tableName.isEmpty())
		{
			Entity e = c.getAnnotation(Entity.class);
			if (e != null)
			{
				tableName = e.name();
			}
		}
		if (tableName.isEmpty())
		{
			tableName = getClass()
			               .getSimpleName();
		}
		return tableName;
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
