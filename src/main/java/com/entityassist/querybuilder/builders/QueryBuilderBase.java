package com.entityassist.querybuilder.builders;

import com.entityassist.BaseEntity;
import com.entityassist.injections.EntityAssistBinder;
import com.entityassist.querybuilder.EntityAssistStrings;
import com.entityassist.querybuilder.QueryBuilder;
import com.entityassist.querybuilder.statements.InsertStatement;
import com.entityassist.services.EntityAssistIDMapping;
import com.google.inject.Key;
import com.entityassist.querybuilder.statements.UpdateStatement;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedpersistence.db.DbStartup;
import com.guicedee.guicedpersistence.services.ITransactionHandler;
import com.guicedee.logger.LogFactory;
import com.oracle.jaxb21.PersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.metamodel.Attribute;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.guicedee.guicedpersistence.scanners.PersistenceServiceLoadersBinder.ITransactionHandlerReader;

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
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public abstract class QueryBuilderBase<J extends QueryBuilderBase<J, E, I>, E extends BaseEntity<E, ? extends QueryBuilder, I>, I extends Serializable>
{
	/**
	 * This logger
	 */
	private static final Logger log = LogFactory.getLog("QueryBuilderBase");
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
	 * If the inserted ID should be request override
	 */
	private boolean requestId = true;

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
	 * @return class of type entity
	 */
	protected Class<E> getEntityClass()
	{
		return entityClass;
	}

	/**
	 * Returns a mapped entity on this builder
	 *
	 * @return the actual entity
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
	public J setEntity(E entity)
	{
		this.entity = entity;
		entityClass = (Class<E>) entity.getClass();
		return (J) this;
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
	 * 		the number of results to skip before loading
	 *
	 * @return This
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
	 * @return int
	 */
	public Integer getMaxResults()
	{
		return maxResults;
	}

	/**
	 * Sets the maximum results to return
	 *
	 * @param maxResults
	 * 		the maximum number of results to return
	 *
	 * @return This
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
	 * <p>
	 * doesn't set run detached - executes flush after persist
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings({"unchecked", "Duplicates"})
	public J persistNow(E entity)
	{
		boolean transactionAlreadyStarted = false;
		PersistenceUnit unit = GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation()));
		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.transactionExists(getEntityManager(), unit))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}

		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.beginTransacation(false, getEntityManager(), unit);
			}
		}
		persist(entity);
		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.commitTransacation(false, getEntityManager(), unit);
			}
		}
		return (J) this;
	}

	/**
	 * Returns the annotation associated with the entity manager
	 *
	 * @return The annotations associated with this builder
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Annotation> getEntityManagerAnnotation()
	{
		EntityManager em = getEntityManager();
		return (Class<? extends Annotation>) em.getProperties()
		                                       .get("annotation");
	}

	/**
	 * Returns the assigned entity manager
	 *
	 * @return The entity manager to use for this run
	 */
	@NotNull
	@Transient
	protected abstract EntityManager getEntityManager();

	/**
	 * Persists this entity. Uses the get instance entity manager to operate.
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J persist(E entity)
	{
		try
		{
			if (onCreate(entity))
			{
				boolean transactionAlreadyStarted = false;
				com.oracle.jaxb21.PersistenceUnit unit = GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation()));
				if (isRunDetached())
				{
					String insertString = new InsertStatement(entity).toString();
					log.finer(insertString);
					if (DbStartup.getAvailableDataSources()
					             .contains(getEntityManagerAnnotation()))
					{
						DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
						if(ds == null)
						{
							Query query = getEntityManager().createNativeQuery(insertString);
							query.executeUpdate();
							if (isIdGenerated() && isRequestId())
							{
								iterateThroughResultSetForGeneratedIDs();
							}
						}
						else

						try (Connection c = ds.getConnection(); Statement st = c.createStatement())
						{
							st.executeUpdate(insertString);
							if (isIdGenerated() && isRequestId())
							{
								iterateThroughResultSetForGeneratedIDs(c);
							}
						}
					}
					else
					{
						Query query = getEntityManager().createNativeQuery(insertString);
						query.executeUpdate();
						if (isIdGenerated() && isRequestId())
						{
							iterateThroughResultSetForGeneratedIDs();
						}
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
	 * <p>
	 * If the library generates the sql and runs it through a native query. Use InsertStatement, SelectStatement, Delete and UpdateStatement to view the queries that will get run
	 *
	 * @return boolean
	 */
	public boolean isRunDetached()
	{
		return runDetached;
	}

	/**
	 * If this entity should run in a detached and separate to the entity manager
	 * <p>
	 * If the library generates the sql and runs it through a native query. Use InsertStatement, SelectStatement, Delete and UpdateStatement to view the queries that will get run
	 *
	 * @param runDetached
	 * 		if must do
	 *
	 * @return This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
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
	 * Getter for property 'requestId'.
	 *
	 * @return Value for property 'requestId'.
	 */
	public boolean isRequestId()
	{
		return requestId;
	}

	/**
	 * Method iterateThroughResultSetForGeneratedIDs ...
	 */
	private void iterateThroughResultSetForGeneratedIDs()
	{
		Query statmentSelectId = getEntityManager().createNativeQuery(selectIdentityString);
		Object o = statmentSelectId.getSingleResult();
		processId(o);
	}

	/**
	 * Method iterateThroughResultSetForGeneratedIDs ...
	 */
	private void iterateThroughResultSetForGeneratedIDs(Connection connection)
	{
		DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
		try (Statement st = connection.createStatement())
		{
			ResultSet rs = st.executeQuery(selectIdentityString);
			if (rs.next())
			{
				Object o = rs.getObject(1);
				processId(o);
			}
		}
		catch (SQLException e)
		{
			log.log(Level.WARNING, "Unable to get generatedID", e);
		}
	}

	/**
	 * Method processId ...
	 *
	 * @param o
	 * 		of type Object
	 */
	@SuppressWarnings("unchecked")
	private void processId(Object o)
	{
		EntityAssistIDMapping mapping = EntityAssistBinder.lookup(o.getClass(), entity.getClassIDType());
		entity.setId((I) mapping.toObject(o));
	}

	/**
	 * Setter for property 'requestId'.
	 *
	 * @param requestId
	 * 		Value to set for property 'requestId'.
	 */
	public void setRequestId(boolean requestId)
	{
		this.requestId = requestId;
	}

	/**
	 * Persist and Flush using the detached method (as a native query)
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings({"unchecked", "Duplicates"})
	public J persistNow(E entity, boolean runDetached)
	{
		boolean transactionAlreadyStarted = false;
		PersistenceUnit unit = GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation()));
		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}

		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.beginTransacation(false, getEntityManager(), unit);
			}
		}
		setRunDetached(runDetached);
		persist(entity);
		for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.commitTransacation(false, getEntityManager(), unit);
			}
		}
		return (J) this;
	}

	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings({"unchecked", "Duplicates"})
	public E update(E entity)
	{
		try
		{
			if (onUpdate(entity))
			{
				if (isRunDetached())
				{
					String insertString = new UpdateStatement(entity).toString();
					log.fine(insertString);
					if (DbStartup.getAvailableDataSources()
					             .contains(getEntityManagerAnnotation()))
					{
						DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
						if(ds == null)
						{
							Query query = getEntityManager().createNativeQuery(insertString);
							query.executeUpdate();
						}
						else

							try (Connection c = ds.getConnection(); Statement st = c.createStatement())
							{
								st.executeUpdate(insertString);
							}
					}
					else
					{
						Query query = getEntityManager().createNativeQuery(insertString);
						query.executeUpdate();
					}
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
			log.log(Level.SEVERE, "Cannot update this entity  unknown exception the state of this object is not ready : \n", e);
		}
		return entity;
	}

	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return This
	 */
	@NotNull
	@SuppressWarnings({"unchecked", "Duplicates"})
	public E updateNow(E entity)
	{
		try
		{
			if (onUpdate(entity))
			{
				boolean transactionAlreadyStarted = false;
				PersistenceUnit unit = GuiceContext.get(Key.get(PersistenceUnit.class, getEntityManagerAnnotation()));
				for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
				{
					if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
					{
						transactionAlreadyStarted = true;
						break;
					}
				}

				for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
				{
					if (!transactionAlreadyStarted && handler.active(unit))
					{
						handler.beginTransacation(false, getEntityManager(), unit);
					}
				}
				if (isRunDetached())
				{
					String insertString = new UpdateStatement(entity).toString();
					log.fine(insertString);
					if (DbStartup.getAvailableDataSources()
					             .contains(getEntityManagerAnnotation()))
					{
						DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
						if(ds == null)
						{
							Query query = getEntityManager().createNativeQuery(insertString);
							query.executeUpdate();
						}
						else

							try (Connection c = ds.getConnection(); Statement st = c.createStatement())
							{
								st.executeUpdate(insertString);
							}
					}
					else
					{
						Query query = getEntityManager().createNativeQuery(insertString);
						query.executeUpdate();
					}
				}
				else
				{
					getEntityManager().merge(entity);
					getEntityManager().flush();
				}
				for (ITransactionHandler handler : GuiceContext.get(ITransactionHandlerReader))
				{
					if (!transactionAlreadyStarted && handler.active(unit))
					{
						handler.commitTransacation(false, getEntityManager(), unit);
					}
				}
			}
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "Cannot update this entity the state of this object is not ready : \n", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Cannot update this entity  unknown exception the state of this object is not ready : \n", e);
		}
		return entity;
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
	 * @return List of Strings
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
				                         .getSimpleName() + EntityAssistStrings.STRING_DOT + contraints.getPropertyPath() + EntityAssistStrings.STRING_EMPTY + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}

	/**
	 * Returns the given attribute for a field name by reflectively accesing the static class
	 *
	 * @param fieldName
	 *
	 * @return
	 */
	public Attribute getAttribute(String fieldName)
	{
		String clazz = getEntityClass().getCanonicalName() + "_";
		try
		{
			Class c = Class.forName(clazz);
			Field f = c.getField(fieldName);
			return (Attribute) f.get(null);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to field field in class [" + clazz + "]-[" + fieldName + "]", e);
		}
		return null;
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
