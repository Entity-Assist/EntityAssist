package za.co.mmagon.entityassist;


import com.armineasy.injection.GuiceContext;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Injector;
import za.co.mmagon.entityassist.converters.LocalDateTimeAttributeConverter;
import za.co.mmagon.entityassist.enumerations.ActiveFlag;
import za.co.mmagon.entityassist.querybuilder.QueryBuilderCore;
import za.co.mmagon.logger.LogFactory;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

/**
 * @param <J> Always this class (CRP)
 * @param <Q> The associated query builder class
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
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	private static final DateTimeFormatter dateTimeOffsetFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	;
	private static final long serialVersionUID = 1L;
	@Transient
	@JsonIgnore
	protected Class<J> myClass;
	@Transient
	@JsonIgnore
	protected Class<Q> queryBuilderClass;
	@Transient
	@JsonIgnore
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
	
	public CoreEntity()
	{
	}
	
	public abstract I getId();
	
	public abstract J setId(I id);
	
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
	
	@PreUpdate
	public void onUpdate()
	{
		setWarehouseLastUpdatedTimestamp(LocalDateTime.now());
	}
	
	/**
	 * Any DB Transient Maps
	 *
	 * @return
	 */
	public Map<Serializable, Serializable> getProperties()
	{
		if (properties == null)
		{
			properties = new HashMap<>();
		}
		return properties;
	}
	
	public void setProperties(Map<Serializable, Serializable> properties)
	{
		this.properties = properties;
	}
	
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
			}
		}
		return myClass;
	}
	
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
			}
		}
		return queryBuilderClass;
	}
	
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
				this.idTypeClass = null;
			}
		}
		return idTypeClass;
	}
	
	public Injector getInjector()
	{
		return GuiceContext.inject();
	}
	
	public boolean isFake()
	{
		return getProperties().containsKey("Fake") ? Boolean.parseBoolean(getProperties().get("Fake").toString()) : false;
	}
	
	public J setFake(boolean fake)
	{
		getProperties().put("Fake", fake);
		return (J) this;
	}
	
	/**
	 * Returns the builder associated with this entity
	 *
	 * @return
	 */
	public Q builder()
	{
		try
		{
			Type s = getClass().getGenericSuperclass();
			ParameterizedType pt = ((ParameterizedType) s);
			Class<J> myClass = getClassEntityType();
			Class<Q> queryBuilderClass = getClassQueryBuilderClass();
			Q wb = GuiceContext.getInstance(queryBuilderClass);
			return wb;
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, null, ex);
		}
		return null;
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
	 * Persist and Flush
	 *
	 * @return
	 */
	public J persistNow()
	{
		persist();
		getEntityManager().flush();
		return (J) this;
	}
	
	/**
	 * Persist
	 *
	 * @return
	 */
	public J persist()
	{
		try
		{
			if (getEffectiveFromDate() == null)
			{
				onCreate();
			}
			
			String insertString = buildInsertString();
			System.out.println(insertString);
			
			EntityManager entityManager = GuiceContext.getInstance(EntityManager.class);
			if (!entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().begin();
			}
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
				//log.log(Level.SEVERE, "Unable to perform insert", sql);
				throw new RuntimeException("Fix the query....", sql);
			}
			entityManager.getTransaction().commit();
			setFake(false);
		}
		catch (IllegalStateException ise)
		{
			//Logger.getLogger("DatabaseBean").log(Level.SEVERE, "Error", ise);
			throw new RuntimeException("whoops", ise);
		}
		catch (Exception e)
		{
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			Set constraintViolations = validator.validate(this);
			
			if (constraintViolations.size() > 0)
			{
				log.severe("Constraint Violations Occured During Persist");
				for (Iterator iterator = constraintViolations.iterator(); iterator.hasNext(); )
				{
					ConstraintViolation contraints = (ConstraintViolation) iterator.next();
					log.log(Level.SEVERE, "{0}.{1} {2}", new Object[]
							{
									contraints.getRootBeanClass().getSimpleName(), contraints.getPropertyPath(), contraints.getMessage()
							});
				}
			}
			
			if (e.getMessage() != null)
			{
				if (e.getMessage().contains(". The duplicate key value is ("))
				{
					log.log(Level.WARNING, "{0}", new Object[]
							{
									"Attempt to insert an already existing entity. Merging instead"
							});
					//merge(object);
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
	
	public J update()
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
			log.log(Level.SEVERE, "Illegal State Exception? : ", ise);
			throw new RuntimeException("whoops", ise);
		}
		catch (Exception e)
		{
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			Set constraintViolations = validator.validate(this);
			
			if (constraintViolations.size() > 0)
			{
				log.severe("Constraint Violations Occured");
				for (Iterator iterator = constraintViolations.iterator(); iterator.hasNext(); )
				{
					ConstraintViolation contraints = (ConstraintViolation) iterator.next();
					log.log(Level.SEVERE, "{0}.{1} {2}", new Object[]
							{
									contraints.getRootBeanClass().getSimpleName(), contraints.getPropertyPath(), contraints.getMessage()
							});
				}
			}
			throw new RuntimeException("whoops", e);
		}
		return (J) this;
	}
	
	public List<String> validateEntity()
	{
		List<String> errors = new ArrayList<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set constraintViolations = validator.validate(this);
		
		if (constraintViolations.size() > 0)
		{
			log.info("Constraint Violations Occured");
			for (Iterator iterator = constraintViolations.iterator(); iterator.hasNext(); )
			{
				ConstraintViolation contraints = (ConstraintViolation) iterator.next();
				String error = contraints.getRootBeanClass().getSimpleName() + "." + contraints.getPropertyPath() + " " + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}
	
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(EntityManager.class);
	}
	
	private String buildInsertString()
	{
		String insertString = "INSERT INTO ";
		CoreEntity table = (CoreEntity) this;
		
		Class c = getClass();
		Table t = getClass().getAnnotation(Table.class);
		String tableName = t.name();
		insertString += tableName + " (";
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
				if (columnName == null || columnName.isEmpty())
				{
					continue;
				}
			/*
				if (o instanceof CoreEntity)
				{
					CoreEntity wct = (CoreEntity) o;
					if (wct.getId() == Long.MAX_VALUE)
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
				}*/
				
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
			insertString += columnName + ", ";
		}
		insertString = insertString.substring(0, insertString.length() - 2);
		insertString += ") VALUES (";
		for (Object columnValue : columnValues)
		{
			if (columnValue instanceof Boolean)
			{
				insertString += (Boolean.class.cast(columnValue) ? "1" : "0") + ", ";
			}
			else if (columnValue instanceof Long)
			{
				insertString += columnValue + ", ";
			}
			else if (columnValue instanceof Integer)
			{
				insertString += columnValue + ", ";
			}
			else if (columnValue instanceof BigInteger)
			{
				insertString += ((BigInteger) columnValue).longValue() + ", ";
			}
			else if (columnValue instanceof BigDecimal)
			{
				insertString += ((BigDecimal) columnValue).doubleValue() + ", ";
			}
			else if (columnValue instanceof Short)
			{
				short columnVal = (short) columnValue;
				insertString += columnVal + ", ";
			}
			else if (columnValue instanceof String)
			{
				insertString += "'" + ((String) columnValue).replaceAll("'", "''") + "', ";
			}
			else if (columnValue instanceof Date)
			{
				Date date = (Date) columnValue;
				insertString += "'" + sdf.format(date) + "', ";
			}
			else if (columnValue instanceof LocalDate)
			{
				LocalDate date = (LocalDate) columnValue;
				insertString += "'" + dateFormat.format(date) + "', ";
			}
			else if (columnValue instanceof LocalDateTime)
			{
				LocalDateTime date = (LocalDateTime) columnValue;
				insertString += "'" + dateTimeFormat.format(date) + "', ";
			}
			else if (columnValue instanceof CoreEntity)
			{
				CoreEntity wct = (CoreEntity) columnValue;
				insertString += wct.getId() + ", ";
			}
			else if (columnValue instanceof Enum)
			{
				Enum wct = (Enum) columnValue;
				insertString += "'" + wct.toString() + "', ";
			}
		}
		
		insertString = insertString.substring(0, insertString.length() - 2);
		insertString += ");";
		
		return insertString;
	}
	
	public LocalDateTime getEffectiveFromDate()
	{
		return effectiveFromDate;
	}
	
	public J setEffectiveFromDate(LocalDateTime effectiveFromDate)
	{
		this.effectiveFromDate = effectiveFromDate;
		return (J) this;
	}
	
	public LocalDateTime getEffectiveToDate()
	{
		return effectiveToDate;
	}
	
	public J setEffectiveToDate(LocalDateTime effectiveToDate)
	{
		this.effectiveToDate = effectiveToDate;
		return (J) this;
	}
	
	public LocalDateTime getWarehouseCreatedTimestamp()
	{
		return warehouseCreatedTimestamp;
	}
	
	public J setWarehouseCreatedTimestamp(LocalDateTime warehouseCreatedTimestamp)
	{
		this.warehouseCreatedTimestamp = warehouseCreatedTimestamp;
		return (J) this;
	}
	
	public LocalDateTime getWarehouseLastUpdatedTimestamp()
	{
		return warehouseLastUpdatedTimestamp;
	}
	
	public J setWarehouseLastUpdatedTimestamp(LocalDateTime warehouseLastUpdatedTimestamp)
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
	public void setReferenceId(String referenceId)
	{
		this.referenceId = referenceId;
	}
	
}
