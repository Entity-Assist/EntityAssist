package za.co.mmagon.entityassist;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Injector;
import za.co.mmagon.entityassist.converters.LocalDateTimeAttributeConverter;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.exceptions.ConstraintsNotMetException;
import za.co.mmagon.entityassist.exceptions.EntityNotValidException;
import za.co.mmagon.entityassist.exceptions.QueryNotValidException;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;
import za.co.mmagon.logger.LogFactory;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.armineasy.injection.GuiceContext.getInstance;
import static com.armineasy.injection.GuiceContext.inject;

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
		implements Serializable
{
	private static final Logger log = LogFactory.getLog(CoreEntity.class.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the date time formatter
	 */
	private static final transient DateTimeFormatter dateTimeOffsetFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	/**
	 * The standard sdf format
	 */
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/**
	 * Returns teh date formatter
	 */
	private final transient DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/**
	 * Returns the date time formmatter
	 */
	private final transient DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	@Transient
	@JsonIgnore
	@SuppressWarnings("all")
	protected Class<J> myClass;
	@Transient
	@JsonIgnore
	@SuppressWarnings("all")
	protected Class<Q> queryBuilderClass;
	@Transient
	@JsonIgnore
	@SuppressWarnings("all")
	protected Class<I> idTypeClass;
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
	@Transient
	@JsonIgnore
	private Map<Serializable, Serializable> properties;

	/**
	 * Initialize the entity
	 */
	@SuppressWarnings("all")
	public CoreEntity()
	{
	}

	/**
	 * If this ID is generated from the source and which form to use
	 * Default is Generated
	 *
	 * @return Returns if the id column is a generated type
	 */
	public abstract boolean isIdGenerated();

	/**
	 * Returns this classes specific entity type
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<J> getClassEntityType()
	{
		if (myClass == null)
		{
			try
			{
				this.myClass = (Class<J>) ((ParameterizedType) getClass()
						                                               .getGenericSuperclass()).getActualTypeArguments()[0];
			}
			catch (Exception e)
			{
				this.myClass = null;
				log.log(Level.SEVERE, "Cannot return the my class generic type? this class is not extended?", e);
			}
		}
		return myClass;
	}

	/**
	 * Returns a quick check to the GuiceContext container
	 *
	 * @return
	 */
	@NotNull
	public Injector getInjector()
	{
		return inject();
	}

	/**
	 * Returns if this entity is operating as a fake or not (testing or dto)
	 *
	 * @return
	 */
	@NotNull
	public boolean isFake()
	{
		return getProperties().containsKey("Fake") && Boolean.parseBoolean(getProperties().get("Fake").toString());
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
	public Map<Serializable, Serializable> getProperties()
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
	public J setProperties(@NotNull Map<Serializable, Serializable> properties)
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
	@NotNull
	public J setFake(boolean fake)
	{
		getProperties().put("Fake", fake);
		return (J) this;
	}

	@NotNull
	/**
	 * Returns the builder associated with this entity
	 *
	 * @return
	 */
	public Q builder()
	{
		Class<Q> foundQueryBuilderClass = getClassQueryBuilderClass();
		return getInstance(foundQueryBuilderClass);

	}

	/**
	 * Returns this classes associated query builder class
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public Class<Q> getClassQueryBuilderClass()
	{
		if (queryBuilderClass == null)
		{
			try
			{
				this.queryBuilderClass = (Class<Q>) ((ParameterizedType) getClass()
						                                                         .getGenericSuperclass()).getActualTypeArguments()[1];
			}
			catch (Exception e)
			{
				this.queryBuilderClass = null;
				log.log(Level.SEVERE, "Cannot return the my query builder class - config seems wrong. Check that a builder is attached to this entity as the second generic field type e.g. \n" +
						                      "public class EntityClass extends CoreEntity<EntityClass, EntityClassBuilder, Long>\n\n" +
						                      "You can view the test class in the sources or at https://github.com/GedMarc/EntityAssist/tree/master/test/za/co/mmagon/entityassist/entities", e);
			}
		}
		return queryBuilderClass;
	}

	/**
	 * Persist and Flush
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	@NotNull
	public J persistNow() throws ConstraintsNotMetException, EntityNotValidException, QueryNotValidException
	{
		persist();
		getEntityManager().flush();
		return (J) this;
	}

	/**
	 * Persists this entity. Uses the get instance entity manager to operate.
	 *
	 * @return
	 * @throws za.co.mmagon.entityassist.exceptions.QueryNotValidException
	 * @throws za.co.mmagon.entityassist.exceptions.EntityNotValidException
	 * @throws za.co.mmagon.entityassist.exceptions.ConstraintsNotMetException
	 */
	@NotNull
	@SuppressWarnings("all")
	public J persist() throws QueryNotValidException, EntityNotValidException, ConstraintsNotMetException
	{
		try
		{
			if (getEffectiveFromDate() == null)
			{
				onCreate();
			}

			String insertString = buildInsertString();
			log.info(insertString);
			EntityManager entityManager = getInstance(EntityManager.class);
			if (entityManager != null && !entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().begin();
				java.sql.Connection connection = entityManager.unwrap(java.sql.Connection.class);
				try (
						    PreparedStatement statement = connection.prepareStatement(insertString,
						                                                              Statement.RETURN_GENERATED_KEYS);)
				{
					int affectedRows = statement.executeUpdate();
					if (affectedRows == 0)
					{
						throw new SQLException("Insert Failed, no rows affected.");
					}
					try (ResultSet generatedKeys = statement.getGeneratedKeys())
					{
						if (generatedKeys.next())
						{
							Object o = generatedKeys.getObject(1);
							if (o instanceof BigDecimal)
							{
								if (getClassIDType().isAssignableFrom(Long.class))
								{
									setId((I) (Long) BigDecimal.class.cast(o).longValue());
								}
								else if (getClassIDType().isAssignableFrom(Integer.class))
								{
									setId((I) (Integer) BigDecimal.class.cast(o).intValue());
								}
								else
								{
									setId((I) generatedKeys.getObject(1));
								}
							}
							else
							{
								setId((I) generatedKeys.getObject(1));
							}
						}
						else
						{
							throw new SQLException("Creating user failed, no ID obtained.");
						}
					}
				}
				catch (SQLException sql)
				{
					throw new QueryNotValidException("Fix the query....", sql);
				}
				entityManager.getTransaction().commit();
			}
			setFake(false);
		}
		catch (IllegalStateException ise)
		{
			throw new EntityNotValidException("This entity is not in a state to be persisted. Perhaps an update merge remove or refresh?", ise);
		}
		catch (Exception e)
		{
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			Set constraintViolations = validator.validate(this);

			if (!constraintViolations.isEmpty())
			{
				log.severe("Constraint Violations Occured During Persist");
				for (Object constraintViolation : constraintViolations)
				{
					ConstraintViolation contraints = (ConstraintViolation) constraintViolation;
					log.log(Level.SEVERE,
					        "{0}.{1} {2}",
					        new Object[]
							        {
									        contraints.getRootBeanClass().getSimpleName(), contraints.getPropertyPath(), contraints.getMessage()
							        });
				}
			}

			if (e.getMessage() != null)
			{
				if (e.getMessage().contains(". The duplicate key value is ("))
				{
					log.log(Level.WARNING,
					        "{0}",
					        new Object[]
							        {
									        "Attempt to insert an already existing entity [" + getClass().getCanonicalName() + "]-[" + getId() + "]. Merging instead."
							        });
					return update();
				}
				else

				{
					throw e;
				}
			}
			throw e;
		}
		return (J) this;
	}

	/**
	 * Returns the assigned entity manager
	 * @return
	 */
	@NotNull
	public EntityManager getEntityManager()
	{
		return getInstance(EntityManager.class);
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
	 * Performs the onCreate method setting the effective to date if it wasn't null, the effective from date if it wasn't set and the active flag derived
	 */
	@PrePersist
	public void onCreate()
	{
		if (getEffectiveToDate() == null)
		{
			setEffectiveToDate(LocalDateTime.of(2999, 12, 31, 23, 59, 59, 999));
		}
		if (getEffectiveFromDate() == null)
		{
			setEffectiveFromDate(LocalDateTime.now());
		}
		setWarehouseCreatedTimestamp(LocalDateTime.now());
		setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
		if (getActiveFlag() == null)
		{
			setActiveFlag(ActiveFlag.Active);
		}
	}

	/**
	 * Finds the entity with the given ID
	 *
	 * @param id
	 *
	 * @return
	 */
	public Optional<J> find(Long id)
	{
		return builder().find(id).select().get();
	}

	/**
	 * Finds all the entity types
	 *
	 * @return
	 */
	public List<J> findAll()
	{
		return builder().select().getAll();
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
	public J setActiveFlag(ActiveFlag activeFlag)
	{
		this.activeFlag = activeFlag;
		return (J) this;
	}

	/**
	 * Builds the physical insert string for this entity class
	 * @return
	 */
	@NotNull
	@SuppressWarnings("all")
	private String buildInsertString()
	{
		StringBuilder insertString = new StringBuilder("INSERT INTO ");
		Class c = getClass();
		Table t = getClass().getAnnotation(Table.class);
		String tableName = t.name();
		insertString.append(tableName).append(" (");
		List<Field> fields = new ArrayList<>();

		Class<?> i = c;
		while (i != null)
		{
			Collections.addAll(fields, i.getDeclaredFields());
			i = i.getSuperclass();
		}
		List<String> columnsNames = new ArrayList<>();
		List<Object> columnValues = new ArrayList<>();

		for (Field field : fields)
		{
			field.setAccessible(true);
			try
			{
				Object o = field.get(this);
				if (o == null)
				{
					continue;
				}

				GeneratedValue genValu = field.getAnnotation(GeneratedValue.class);
				if (genValu != null)
				{
					continue;
				}

				JoinColumn joinCol = field.getAnnotation(JoinColumn.class);
				Column col = field.getAnnotation(Column.class);
				if (col == joinCol) //fuzzy logic, if both null continue
				{
					continue;
				}
				String columnName = col == null ? joinCol.name() : col.name();
				if (columnName.isEmpty())
				{
					continue;
				}

				if (o instanceof CoreEntity)
				{
					CoreEntity wct = (CoreEntity) o;
					if (Long.class.cast(wct.getId()) == Long.MAX_VALUE)
					{
						continue;
					}
				}
				else if (o instanceof Long)
				{
					Long wct = (Long) o;
					if (wct == Long.MAX_VALUE)
					{
						continue;
					}
				}

				if (!columnsNames.contains(columnName))
				{
					columnsNames.add(columnName);
					columnValues.add(o);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException ex)
			{
				log.log(Level.SEVERE, null, ex);
			}
		}

		//columns
		for (String columnName : columnsNames)
		{
			insertString.append(columnName).append(", ");
		}
		insertString.delete(insertString.length() - 2, insertString.length());
		insertString.append(") VALUES (");
		for (Object columnValue : columnValues)
		{
			if (columnValue instanceof Boolean)
			{
				insertString.append(Boolean.class.cast(columnValue) ? "1" : "0").append(", ");
			}
			else if (columnValue instanceof Long)
			{
				insertString.append(columnValue).append(", ");
			}
			else if (columnValue instanceof Integer)
			{
				insertString.append(columnValue).append(", ");
			}
			else if (columnValue instanceof BigInteger)
			{
				insertString.append(((BigInteger) columnValue).longValue()).append(", ");
			}
			else if (columnValue instanceof BigDecimal)
			{
				insertString.append(((BigDecimal) columnValue).doubleValue()).append(", ");
			}
			else if (columnValue instanceof Short)
			{
				short columnVal = (short) columnValue;
				insertString.append(columnVal).append(", ");
			}
			else if (columnValue instanceof String)
			{
				insertString.append("'").append(((String) columnValue).replaceAll("'", "''")).append("', ");
			}
			else if (columnValue instanceof Date)
			{
				Date date = (Date) columnValue;
				insertString.append("'").append(sdf.format(date)).append("', ");
			}
			else if (columnValue instanceof LocalDate)
			{
				LocalDate date = (LocalDate) columnValue;
				insertString.append("'").append(dateFormat.format(date)).append("', ");
			}
			else if (columnValue instanceof LocalDateTime)
			{
				LocalDateTime date = (LocalDateTime) columnValue;
				insertString.append("'").append(dateTimeFormat.format(date)).append("', ");
			}
			else if (columnValue instanceof CoreEntity)
			{
				CoreEntity wct = (CoreEntity) columnValue;
				insertString.append(wct.getId()).append(", ");
			}
			else if (columnValue instanceof Enum)
			{
				Enum wct = (Enum) columnValue;
				insertString.append("'").append(wct.toString()).append("', ");
			}
		}

		insertString.delete(insertString.length() - 2, insertString.length());
		insertString.append(");");

		return insertString.toString();
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
		if (idTypeClass == null)
		{
			try
			{
				this.idTypeClass = (Class<I>) ((ParameterizedType) getClass()
						                                                   .getGenericSuperclass()).getActualTypeArguments()[2];
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "Cannot return the class for uncheckeds. Embeddables are allowed. Config seems wrong. Check that a builder is attached to this entity as the second generic field type e.g. \n" +
						                      "public class EntityClass extends CoreEntity<EntityClass, EntityClassBuilder, Long>\n\n" +
						                      "You can view the test class in the sources or at https://github.com/GedMarc/EntityAssist/tree/master/test/za/co/mmagon/entityassist/entities", e);
				this.idTypeClass = null;
			}
		}
		return idTypeClass;
	}

	/**
	 * Returns the id of the given type in the generic decleration
	 *
	 * @return Returns the ID
	 */
	public abstract I getId();

	/**
	 * Returns the id of the given type in the generic decleration
	 *
	 * @param id
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	public abstract J setId(I id);

	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("all")
	public J update() throws EntityNotValidException, ConstraintsNotMetException
	{
		try
		{
			onUpdate();
			if (!(getEntityManager().getTransaction().isActive()))
			{
				getEntityManager().getTransaction().begin();
			}
			getEntityManager().merge(this);
			getEntityManager().flush();
			getEntityManager().getTransaction().commit();
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "? : ", ise);
			throw new EntityNotValidException("Cannot update this entity the state of this object is not ready [" + getClass().getCanonicalName() + "}]-[" + getId() + "]", ise);
		}
		catch (Exception e)
		{
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			Set constraintViolations = validator.validate(this);

			if (!constraintViolations.isEmpty())
			{
				log.severe("Constraint Violations Occured\n");
				for (Object constraintViolation : constraintViolations)
				{
					ConstraintViolation contraints = (ConstraintViolation) constraintViolation;
					log.log(Level.SEVERE,
					        "{0}.{1} {2}",
					        new Object[]
							        {
									        contraints.getRootBeanClass().getSimpleName(), contraints.getPropertyPath(), contraints.getMessage()
							        });
				}
			}
			throw new ConstraintsNotMetException("whoops", e);
		}
		return (J) this;
	}

	/**
	 * Returns the effice to date setting for active flag calculation
	 *
	 * @return
	 */
	@Nullable
	@SuppressWarnings("all")
	public LocalDateTime getEffectiveToDate()
	{
		return effectiveToDate;
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
	 * Performs the constraint validation and returns a list of all constraint errors.
	 * <p>
	 * <b>Great for form checking</b>
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unused")
	public List<String> validateEntity()
	{
		List<String> errors = new ArrayList<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set constraintViolations = validator.validate(this);

		if (!constraintViolations.isEmpty())
		{
			log.info("Constraint Violations Occured \n");
			for (Object constraintViolation : constraintViolations)
			{
				ConstraintViolation contraints = (ConstraintViolation) constraintViolation;
				String error = contraints.getRootBeanClass().getSimpleName() + "." + contraints.getPropertyPath() + " " + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}

	/**
	 * Returns the warehouse created timestamp column value
	 *
	 * @return
	 */
	@Nullable
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
	@Nullable
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
	@Nullable
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
	 * Returns the sdf format
	 *
	 * @return
	 */
	@NotNull
	public SimpleDateFormat getDefaultDateTimeFormatter()
	{
		return sdf;
	}

	/**
	 * Returns the date time formatter for LocalDate instances
	 *
	 * @return
	 */
	@NotNull
	public DateTimeFormatter getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * Return the date time formatter for LocalDateTime instances
	 *
	 * @return
	 */
	@NotNull
	public DateTimeFormatter getDateTimeFormat()
	{
		return dateTimeFormat;
	}

	/**
	 * Returns the formatter for date time offset (sql server)
	 *
	 * @return
	 */
	@NotNull
	public DateTimeFormatter getDateTimeOffsetFormatter()
	{
		return dateTimeOffsetFormatter;
	}
}
