package za.co.mmagon.entityassist.querybuilder.builders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import za.co.mmagon.entityassist.BaseEntity;
import za.co.mmagon.entityassist.enumerations.Provider;
import za.co.mmagon.entityassist.querybuilder.statements.InsertStatement;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.metamodel.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static za.co.mmagon.entityassist.querybuilder.EntityAssistStrings.STRING_SINGLE_QUOTES;
import static za.co.mmagon.entityassist.querybuilder.EntityAssistStrings.STRING_SINGLE_QUOTES_TWICE;

/**
 * Builds a Query Base
 *
 * @param <J>
 * 		This class type
 * @param <E>
 * 		The entity type
 * @param <I>
 * 		The entity ID type
 */
public abstract class QueryBuilderBase<J extends QueryBuilderBase<J, E, I>, E extends BaseEntity<E, J, I>, I extends Serializable>
{

	private static final Logger log = Logger.getLogger("QueryBuilderCore");
	/**
	 * The maximum number of results
	 */
	private Integer maxResults;
	/**
	 * The minimum number of results
	 */
	private Integer firstResults;
	/**
	 * The given entity class
	 */
	private Class<E> entityClass;
	/**
	 * Whether or not to log the rendered select sql
	 */
	private boolean logSelectSql;
	/**
	 * Whether or not to log the insert sql statement
	 */
	private boolean logInsertSql;

	/**
	 * Static provider to generate sql for
	 */
	private Provider provider;

	private E entity;

	/**
	 * Whether or not to run these queries as detached objects or within the entity managers scope
	 */
	@JsonIgnore
	@Transient
	private boolean runDetached;

	@SuppressWarnings("unchecked")
	protected QueryBuilderBase()
	{
		this.entityClass = getEntityClass();
		provider = Provider.Hibernate5jre8;
	}

	@SuppressWarnings("unchecked")
	public void setEntity(Object entity)
	{
		this.entity = (E) entity;
	}

	/**
	 * Returns the associated entity class for this builder
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<E> getEntityClass()
	{
		if (entityClass == null)
		{
			try
			{
				this.entityClass = (Class<E>) ((ParameterizedType) getClass()
						                                                   .getGenericSuperclass()).getActualTypeArguments()[1];
			}
			catch (Exception e)
			{
				this.entityClass = null;
				log.log(Level.SEVERE, "Unable to read the entity class that this query builder core is built for\n", e);
			}
		}
		return entityClass;
	}

	/**
	 * Returns the current set first results
	 *
	 * @return
	 */
	@Nullable
	public Integer getFirstResults()
	{
		return firstResults;
	}

	/**
	 * Sets the first restults to return
	 *
	 * @param firstResults
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setFirstResults(Integer firstResults)
	{
		this.firstResults = firstResults;
		return (J) this;
	}

	/**
	 * Returns the current set maximum results
	 *
	 * @return
	 */
	@Nullable
	public Integer getMaxResults()
	{
		return maxResults;
	}

	/**
	 * Sets the maximum results to return
	 *
	 * @param maxResults
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setMaxResults(Integer maxResults)
	{
		this.maxResults = maxResults;
		return (J) this;
	}

	/**
	 * Returns the current sql generator provider
	 *
	 * @return
	 */
	public Provider getProvider()
	{
		return provider;
	}

	/**
	 * Returns the current sql generator provider
	 *
	 * @param provider
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setProvider(@NotNull Provider provider)
	{
		this.provider = provider;
		return (J) this;
	}

	/**
	 * Whether or not the select queries should be logged out
	 *
	 * @return
	 */
	protected boolean isLogSelectSql()
	{
		return logSelectSql;
	}

	/**
	 * Whether or not the select queries should be logged out
	 *
	 * @param logSelectSql
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected J setLogSelectSql(boolean logSelectSql)
	{
		this.logSelectSql = logSelectSql;
		return (J) this;
	}

	/**
	 * Whether or not the insert sql must be logged
	 *
	 * @return
	 */
	protected boolean isLogInsertSql()
	{
		return logInsertSql;
	}

	/**
	 * Whether or not to insert sql must be logged
	 *
	 * @param logInsertSql
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected J setLogInsertSql(boolean logInsertSql)
	{
		this.logInsertSql = logInsertSql;
		return (J) this;
	}

	/**
	 * Persist and Flush
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	@NotNull
	public J persistNow()
	{
		persist();
		getEntityManager().flush();
		return (J) this;
	}

	/**
	 * Persists this entity. Uses the get instance entity manager to operate.
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("all")
	public J persist()
	{
		try
		{
			onCreate();
			List<String> errors = validateEntity();
			if (!errors.isEmpty())
			{
				log.warning("Constraint Errors Occured, Validation might fail : " + errors);
			}
			String insertString = InsertStatement.buildInsertString(this);
			log.info(insertString);
			EntityManager entityManager = getEntityManager();
			if (!entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().begin();
			}
			if (!isRunDetached())
			{
				entityManager.createNativeQuery(insertString).executeUpdate();
				if (isIdGenerated())
				{
					//Query statmentSelectId = entityManager.createNativeQuery("CALL SCOPE_IDENTITY();"); //h2
					//Query statmentSelectId = entityManager.createNativeQuery("SELECT @@SCOPE_INDENTITY"); //mysql
					Query statmentSelectId = entityManager.createNativeQuery("SELECT @@IDENTITY"); //microsoft sql
					BigDecimal generatedId = ((BigDecimal) statmentSelectId.getSingleResult());
					entity.setId((I) (Long) generatedId.longValue());
					entityManager.getTransaction().commit();
				}
			}
			else
			{
				log.warning("Still doing detached inserts, you can use the performInsert() method currently to generate the sql statement.");
			}
			entity.setFake(false);
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "This entity is not in a state to be persisted. Perhaps an update merge remove or refresh?", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to persist, exception occured\n", e);
		}
		return (J) this;
	}

	/**
	 * Returns the assigned entity manager
	 *
	 * @return
	 */
	@NotNull
	@Transient
	protected abstract EntityManager getEntityManager();

	protected abstract void onCreate();

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
	 * If this entity should run in a detached and separate to the entity manager
	 *
	 * @return
	 */
	public boolean isRunDetached()
	{
		return runDetached;
	}

	/**
	 * If this ID is generated from the source and which form to use
	 * Default is Generated
	 *
	 * @return Returns if the id column is a generated type
	 */
	protected abstract boolean isIdGenerated();

	/**
	 * If this entity should run in a detached and separate to the entity manager
	 *
	 * @param runDetached
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setRunDetached(boolean runDetached)
	{
		this.runDetached = runDetached;
		return (J) this;
	}

	/**
	 * Performs the actual insert
	 *
	 * @param connection
	 * @param insertString
	 */
	public void performInsert(Connection connection, String insertString)
	{
		String escaped = StringUtils.replace(insertString, STRING_SINGLE_QUOTES, STRING_SINGLE_QUOTES_TWICE);
		try (PreparedStatement statement = connection.prepareStatement(escaped, Statement.RETURN_GENERATED_KEYS))
		{
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0)
			{
				throw new SQLException("Insert Failed, no rows affected.");
			}
			try (ResultSet generatedKeys = statement.getGeneratedKeys())
			{
				iterateThroughResultSetForGeneratedIDs(generatedKeys);
			}
		}
		catch (SQLException sql)
		{
			log.log(Level.SEVERE, "Fix the query....", sql);
		}
	}

	private void iterateThroughResultSetForGeneratedIDs(ResultSet generatedKeys) throws SQLException
	{
		if (generatedKeys.next())
		{
			Object o = generatedKeys.getObject(1);
			processId(generatedKeys, o);
		}
		else
		{
			throw new SQLException("Creating user failed, no ID obtained.");
		}
	}

	@SuppressWarnings("unchecked")
	private void processId(ResultSet generatedKeys, Object o) throws SQLException
	{
		if (o instanceof BigDecimal)
		{
			if (entity.getClassIDType().isAssignableFrom(Long.class))
			{
				entity.setId((I) (Long) BigDecimal.class.cast(o).longValue());
			}
			else if (entity.getClassIDType().isAssignableFrom(Integer.class))
			{
				entity.setId((I) (Integer) BigDecimal.class.cast(o).intValue());
			}
			else
			{
				entity.setId((I) generatedKeys.getObject(1));
			}
		}
		else
		{
			entity.setId((I) generatedKeys.getObject(1));
		}
	}

	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("all")
	public J update()
	{
		try
		{
			entity.onUpdate();

			List<String> errors = validateEntity();
			if (!errors.isEmpty())
			{
				throw new SQLException("Constraint Violations in Update");
			}

			if (isRunDetached())
			{
				if (!(getEntityManager().getTransaction().isActive()))
				{
					getEntityManager().getTransaction().begin();
				}
				getEntityManager().merge(this);
				getEntityManager().flush();
				getEntityManager().getTransaction().commit();
			}
			else
			{
				getEntityManager().merge(this);
			}
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "Cannot update this entity the state of this object is not ready : \n", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Cannot update this entity the state of this object is not ready : \n", e);
		}
		return (J) this;
	}


	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isSingularAttribute(Attribute attribute)
	{
		return SingularAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the attribute is plural or map
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isPluralOrMapAttribute(Attribute attribute)
	{
		return isPluralAttribute(attribute) || isMapAttribute(attribute);
	}

	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isPluralAttribute(Attribute attribute)
	{
		return PluralAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the class is a singular attribute
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isMapAttribute(Attribute attribute)
	{
		return MapAttribute.class.isAssignableFrom(attribute.getClass());
	}

	/**
	 * Returns if the attribute is plural or map
	 *
	 * @param attribute
	 *
	 * @return
	 */
	protected boolean isCollectionAttribute(Attribute attribute)
	{
		return CollectionAttribute.class.isAssignableFrom(attribute.getClass());
	}
}
