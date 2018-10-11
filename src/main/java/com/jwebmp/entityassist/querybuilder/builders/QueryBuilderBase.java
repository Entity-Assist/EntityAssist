package com.jwebmp.entityassist.querybuilder.builders;

import com.google.inject.Key;
import com.jwebmp.entityassist.BaseEntity;
import com.jwebmp.entityassist.querybuilder.QueryBuilder;
import com.jwebmp.entityassist.querybuilder.statements.InsertStatement;
import com.jwebmp.guicedinjection.GuiceContext;
import com.jwebmp.guicedpersistence.services.ITransactionHandler;
import com.oracle.jaxb21.PersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jwebmp.entityassist.querybuilder.EntityAssistStrings.*;
import static com.jwebmp.guicedpersistence.scanners.PersistenceServiceLoadersBinder.*;

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
abstract class QueryBuilderBase<J extends QueryBuilderBase<J, E, I>, E extends BaseEntity<E, ? extends QueryBuilder, I>, I extends Serializable>
{
	private static final Logger log = Logger.getLogger("QueryBuilderBase");
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
	 * A Query to execute to return any generated ID
	 */
	private String selectIdentityString = "SELECT @@IDENTITY";
	/**
	 * The actual entity event
	 */
	private E entity;

	/**
	 * Whether or not to run these queries as detached objects or within the entity managers scope
	 */
	@Transient
	private boolean runDetached;

	/**
	 * Constructor QueryBuilderBase creates a new QueryBuilderBase instance.
	 */
	protected QueryBuilderBase()
	{
		entityClass = getEntityClass();
	}

	/**
	 * Returns the associated entity class for this builder
	 *
	 * @return
	 */
	protected Class<E> getEntityClass()
	{
		return entityClass;
	}

	/**
	 * Returns a mapped entity on this builder
	 *
	 * @return
	 */
	public E getEntity()
	{
		return entity;
	}

	/**
	 * Sets the entity for this particular builder
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return J This object
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setEntity(Object entity)
	{
		this.entity = (E) entity;
		entityClass = (Class<E>) entity.getClass();
		return (J) this;
	}

	/**
	 * Method setEntity sets the entity of this QueryBuilderBase object.
	 *
	 * @param entity
	 * 		the entity of this QueryBuilderBase object.
	 */
	public void setEntity(E entity)
	{
		this.entity = entity;
	}

	/**
	 * Returns the current set first results
	 *
	 * @return where to start the first results
	 */
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
	@NotNull
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
	@NotNull
	public J setMaxResults(Integer maxResults)
	{
		this.maxResults = maxResults;
		return (J) this;
	}

	/**
	 * Persist and Flush
	 *
	 * @return
	 */
	@NotNull
	public J persistNow(E entity)
	{
		boolean transactionAlreadyStarted = false;
		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.transactionExists(getEntityManager(), GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation()))))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}

		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation()))))
			{
				handler.beginTransacation(false, getEntityManager(), GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation())));
			}
		}
		persist(entity);
		getEntityManager().flush();

		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation()))))
			{
				handler.commitTransacation(false, getEntityManager(), GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation())));
			}
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

	/**
	 * Returns the annotation associated with the entity manager
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Annotation> getEntityManagerAnnotation()
	{
		EntityManager em = getEntityManager();
		return (Class<? extends Annotation>) em.getProperties()
		                                       .get("annotation");
	}

	/**
	 * Persists this entity. Uses the get instance entity manager to operate.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J persist(E entity)
	{
		try
		{
			if (onCreate(entity))
			{
				if (isRunDetached())
				{
					String insertString = InsertStatement.buildInsertString(entity);
					log.fine(insertString);
					Query query = getEntityManager().createNativeQuery(insertString);
					query.executeUpdate();
					if (isIdGenerated())
					{
						iterateThroughResultSetForGeneratedIDs();
					}
				}
				else
				{
					getEntityManager().persist(entity);
				}
				entity.setFake(false);
			}
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "This entity is not in a state to be persisted. Perhaps an update merge remove or refresh?", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to persist, exception occured\n", e);
			throw new UnsupportedOperationException(e);
		}
		return (J) this;
	}

	/**
	 * Performed on create/persist
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return true if must still create
	 */
	protected boolean onCreate(E entity)
	{
		return true;
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
	 * If this entity should run in a detached and separate to the entity manager
	 *
	 * @param runDetached
	 *
	 * @return
	 */
	public J setRunDetached(boolean runDetached)
	{
		this.runDetached = runDetached;
		return (J) this;
	}

	/**
	 * If this ID is generated from the source and which form to use
	 * Default is Generated
	 *
	 * @return Returns if the id column is a generated type
	 */
	protected abstract boolean isIdGenerated();

	/**
	 * Method iterateThroughResultSetForGeneratedIDs ...
	 *
	 * @throws SQLException
	 * 		when
	 */
	private void iterateThroughResultSetForGeneratedIDs() throws SQLException
	{
		Query statmentSelectId = getEntityManager().createNativeQuery(selectIdentityString);
		Object o = statmentSelectId.getSingleResult();
		processId(o);
	}

	/**
	 * Method processId ...
	 *
	 * @param o
	 * 		of type Object
	 *
	 * @throws SQLException
	 * 		when
	 */
	@SuppressWarnings("unchecked")
	private void processId(Object o) throws SQLException
	{
		if (BigInteger.class.isAssignableFrom(o.getClass()))
		{
			BigInteger actual = (BigInteger) o;
			switch (entity.getClassIDType()
			              .getSimpleName())
			{
				case "BigInteger":
				{
					entity.setId((I) actual);
					break;
				}
				case "Integer":
				{
					entity.setId((I) (Integer) actual.intValue());
					break;
				}
				case "Long":
				{
					entity.setId((I) (Long) actual.longValue());
					break;
				}
				case "BigDecimal":
				{
					entity.setId((I) new BigDecimal(actual));
					break;
				}
			}
		}
		else if (BigDecimal.class.isAssignableFrom(o.getClass()))
		{
			BigDecimal actual = (BigDecimal) o;
			switch (entity.getClassIDType()
			              .getSimpleName())
			{
				case "BigInteger":
				{
					entity.setId((I) actual.unscaledValue());
					break;
				}
				case "Integer":
				{
					entity.setId((I) (Integer) actual.intValue());
					break;
				}
				case "BigDecimal":
				{
					entity.setId((I) actual);
					break;
				}
				case "Long":
				{
					entity.setId((I) (Long) actual.longValue());
					break;
				}
			}
		}
		else if (Long.class.isAssignableFrom(o.getClass()))
		{
			Long actual = (Long) o;
			switch (entity.getClassIDType()
			              .getSimpleName())
			{
				case "BigInteger":
				{
					entity.setId((I) BigInteger.valueOf(actual));
					break;
				}
				case "Integer":
				{
					entity.setId((I) (Integer) actual.intValue());
					break;
				}
				case "BigDecimal":
				{
					entity.setId((I) BigDecimal.valueOf(actual));
					break;
				}
				case "Long":
				{
					entity.setId((I) actual);
					break;
				}
			}
		}
		else if (Integer.class.isAssignableFrom(o.getClass()))
		{
			Integer actual = (Integer) o;
			switch (entity.getClassIDType()
			              .getSimpleName())
			{
				case "BigInteger":
				{
					entity.setId((I) BigInteger.valueOf(actual));
					break;
				}
				case "Integer":
				{
					entity.setId((I) actual);
					break;
				}
				case "BigDecimal":
				{
					entity.setId((I) BigDecimal.valueOf(actual));
					break;
				}
				case "Long":
				{
					entity.setId((I) actual);
					break;
				}
			}
		}
		else if (String.class.isAssignableFrom(o.getClass()))
		{
			String actual = (String) o;
			switch (entity.getClassIDType()
			              .getSimpleName())
			{
				case "BigInteger":
				{
					entity.setId((I) BigInteger.valueOf(Long.parseLong(actual)));
					break;
				}
				case "Integer":
				{
					entity.setId((I) (Integer) Integer.parseInt(actual));
					break;
				}
				case "BigDecimal":
				{
					entity.setId((I) BigDecimal.valueOf(Long.parseLong(actual)));
					break;
				}
				case "Long":
				{
					entity.setId((I) (Long) Long.parseLong(actual));
					break;
				}
				case "String":
				{
					entity.setId((I) actual);
					break;
				}
				case "UUID":
				{
					entity.setId((I) UUID.fromString(actual));
					break;
				}
			}
		}
		else
		{
			log.warning("Cannot set the generated ID");
		}
	}

	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return
	 */
	@NotNull
	public J update(E entity)
	{
		try
		{
			if (onUpdate(entity))
			{
				if (isRunDetached())
				{
					getEntityManager().merge(entity);
				}
				else
				{
					getEntityManager().merge(entity);
				}
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
	 * Performed on update/persist
	 *
	 * @param entity
	 * 		The entity
	 *
	 * @return true if must carry on updating
	 */
	protected boolean onUpdate(E entity)
	{
		return true;
	}

	/**
	 * Performs the constraint validation and returns a list of all constraint errors.
	 *
	 * <b>Great for form checking</b>
	 *
	 * @return
	 */
	@NotNull
	public List<String> validateEntity(E entity)
	{
		List<String> errors = new ArrayList<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set constraintViolations = validator.validate(entity);

		if (!constraintViolations.isEmpty())
		{
			for (Object constraintViolation : constraintViolations)
			{
				ConstraintViolation contraints = (ConstraintViolation) constraintViolation;
				String error = contraints.getRootBeanClass()
				                         .getSimpleName() + STRING_DOT + contraints.getPropertyPath() + STRING_EMPTY + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}

	/**
	 * Method getSelectIdentityString returns the selectIdentityString of this QueryBuilderBase object.
	 *
	 * @return the selectIdentityString (type String) of this QueryBuilderBase object.
	 */
	public String getSelectIdentityString()
	{
		return selectIdentityString;
	}

	/**
	 * Method setSelectIdentityString sets the selectIdentityString of this QueryBuilderBase object.
	 *
	 * @param selectIdentityString
	 * 		the selectIdentityString of this QueryBuilderBase object.
	 */
	public void setSelectIdentityString(String selectIdentityString)
	{
		this.selectIdentityString = selectIdentityString;
	}
}
