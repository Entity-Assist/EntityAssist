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
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class RootEntity<J extends RootEntity<J, Q, I>, Q extends QueryBuilderRoot<Q, J, I>, I extends Serializable> implements IRootEntity<J, Q, I>
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
			now.set(now
					        .get()
					        .plusSeconds(1L));
		}
	}
	
	public static void tickMilli()
	{
		if (now.get() != null)
		{
			now.set(now
					        .get()
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
			instance = foundQueryBuilderClass
					           .getDeclaredConstructor()
					           .newInstance();
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
			if (t.name() == null || t
					                        .name()
					                        .isEmpty() || t
							                                      .name()
							                                      .isBlank())
			{
				tableName += c.getSimpleName();
			}
			else
			{
				tableName += t.name();
			}
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
			tableName = getClass().getSimpleName();
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
	
	/**
	 * Returns a column field for a given name
	 *
	 * @param columnName
	 *
	 * @return
	 */
	public Field getColumn(String columnName)
	{
		for (Field field : getFields())
		{
			if (columnName.equalsIgnoreCase(getColumnName(field)))
			{
				return field;
			}
		}
		throw new UnsupportedOperationException("Invalid Column Name to Find - " + columnName);
	}
	
	public List<Field> getFields()
	{
		List<Field> fields = new ArrayList<>();
		Class<?> i = getClass();
		while (i != null)
		{
			Collections.addAll(fields, i.getDeclaredFields());
			i = i.getSuperclass();
		}
		return fields;
	}
	
	
	public Optional<String> getIdColumnName()
	{
		for (Field field : getFields())
		{
			if (field.isAnnotationPresent(Transient.class) || Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
			{
				continue;
			}
			Id idCol = field.getAnnotation(Id.class);
			EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
			if (idCol != null || embId != null)
			{
				field.setAccessible(true);
				return Optional.ofNullable(getColumnName(field));
			}
		}
		return Optional.empty();
	}
	
	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	protected boolean isColumnReadable(Field field)
	{
		JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
		Column col = field.getAnnotation(Column.class);
		Id idCol = field.getAnnotation(Id.class);
		EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
		OneToOne oneToOne = field.getAnnotation(OneToOne.class);
		OneToMany oneToMany = field.getAnnotation(OneToMany.class);
		ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
		ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
		GeneratedValue genVal = field.getAnnotation(GeneratedValue.class);
		if (col == joinCol && joinCol == idCol && idCol == embId && joinCol == oneToOne && joinCol == oneToMany && joinCol == manyToMany && joinCol == manyToOne && joinCol == genVal) //if everything is null go to next field, easier than is nulls
		{
			return false;
		}
		if (Collection.class.isAssignableFrom(field.getType()))
		{
			return false;
		}
		return true;
	}
	
	public String getColumnName(Field field)
	{
		JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
		Column col = field.getAnnotation(Column.class);
		EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
		String columnName = "";
		
		if (joinCol != null)
		{
			columnName = joinCol.name();
		}
		else if (col != null)
		{
			columnName = col.name();
		}
		else if (embId != null)
		{
			columnName = "";
		}
		
		if (embId != null)
		{
			try
			{
				Object o = field.get(this);
				Field[] f = o
						            .getClass()
						            .getDeclaredFields();
				StringBuilder colNames = new StringBuilder();
				for (Field field1 : f)
				{
					if (isColumnReadable(field1))
					{
						colNames
								.append(getColumnName(field1))
								.append(",");
					}
				}
				colNames.deleteCharAt(colNames.length() - 1);
				return colNames.toString();
			}
			catch (IllegalAccessException e)
			{
				columnName = "";
			}
		}
		if (columnName.isEmpty())
		{
			columnName = field.getName();
		}
		return columnName;
	}
}
