package com.entityassist.querybuilder.builders;

import com.entityassist.*;
import com.entityassist.injections.*;
import com.entityassist.querybuilder.statements.*;
import com.entityassist.services.*;
import com.entityassist.services.querybuilders.*;
import com.google.inject.*;
import com.guicedee.guicedinjection.*;
import com.guicedee.guicedpersistence.services.*;
import com.guicedee.logger.*;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.hibernate.jpa.boot.internal.*;

import javax.sql.*;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import static com.guicedee.guicedinjection.json.StaticStrings.*;
import static com.guicedee.guicedpersistence.scanners.PersistenceServiceLoadersBinder.*;

/**
 * Builds a Query Base
 *
 * @param <J> This class type
 * @param <E> The entity type
 * @param <I> The entity ID type
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public abstract class QueryBuilderRoot<J extends QueryBuilderRoot<J, E, I>,
		E extends RootEntity<E, J, I>,
		I extends Serializable>
		implements IQueryBuilderRoot<J, E, I>
{
	public static Level defaultLoggingLevel = Level.FINER;
	
	/**
	 * This logger
	 */
	private static final Logger log = LogFactory.getLog("QueryBuilderRoot");
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
	private boolean runDetached;
	/**
	 * If the inserted ID should be request override
	 */
	private boolean requestId = true;
	
	private boolean useDirectConnection = false;
	
	private boolean commitDirectConnection;
	
	/**
	 * Constructor QueryBuilderBase creates a new QueryBuilderBase instance.
	 */
	protected QueryBuilderRoot()
	{
		entityClass = getEntityClass();
	}
	
	/**
	 * Returns the associated entity class for this builder
	 *
	 * @return class of type entity
	 */
	@Override
	public Class<E> getEntityClass()
	{
		return entityClass;
	}
	
	/**
	 * Returns a mapped entity on this builder
	 *
	 * @return the actual entity
	 */
	@Override
	public E getEntity()
	{
		return entity;
	}
	
	/**
	 * Sets the entity for this particular builder
	 *
	 * @param entity The entity
	 * @return J This object
	 */
	@Override
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
	@Override
	public Integer getFirstResults()
	{
		return firstResults;
	}
	
	/**
	 * Sets the first restults to return
	 *
	 * @param firstResults the number of results to skip before loading
	 * @return This
	 */
	@Override
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
	@Override
	public Integer getMaxResults()
	{
		return maxResults;
	}
	
	/**
	 * Sets the maximum results to return
	 *
	 * @param maxResults the maximum number of results to return
	 * @return This
	 */
	@Override
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
	@Override
	@NotNull
	@SuppressWarnings({"unchecked", "Duplicates"})
	public J persistNow(E entity)
	{
		boolean transactionAlreadyStarted = false;
		ParsedPersistenceXmlDescriptor unit = GuiceContext.get(Key.get(ParsedPersistenceXmlDescriptor.class, getEntityManagerAnnotation()));
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}
		
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.beginTransacation(false, getEntityManager(), unit);
			}
		}
		persist(entity);
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
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
	@Override
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
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J persist(E entity)
	{
		try
		{
			if (onCreate(entity))
			{
				boolean transactionAlreadyStarted = false;
				ParsedPersistenceXmlDescriptor unit = GuiceContext.get(Key.get(ParsedPersistenceXmlDescriptor.class, getEntityManagerAnnotation()));
				if (isRunDetached())
				{
					String insertString = new InsertStatement(entity).toString();
					log.log(defaultLoggingLevel, insertString);
					if (PersistenceServicesModule.getJtaConnectionBaseInfo()
					                             .containsKey(getEntityManagerAnnotation()))
					{
						DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
						if (!useDirectConnection || ds == null)
						{
							Query query = getEntityManager().createNativeQuery(insertString);
							query.executeUpdate();
							if (isIdGenerated() && isRequestId())
							{
								iterateThroughResultSetForGeneratedIDs();
							}
						}
						else
						{
							var c = GuiceContext.get(Key.get(Connection.class, getEntityManagerAnnotation()));
							try (Statement st = c.createStatement())
							{
								st.executeUpdate(insertString);
								if (isIdGenerated() && isRequestId())
								{
									iterateThroughResultSetForGeneratedIDs(c);
								}
							}
							if(!c.getAutoCommit() && commitDirectConnection)
							{
								c.commit();
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
	 * @param entity The entity
	 * @return true if must still create
	 */
	@Override
	public boolean onCreate(E entity)
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
	@Override
	public boolean isRunDetached()
	{
		return runDetached;
	}
	
	/**
	 * If this entity should run in a detached and separate to the entity manager
	 * <p>
	 * If the library generates the sql and runs it through a native query. Use InsertStatement, SelectStatement, Delete and UpdateStatement to view the queries that will get run
	 *
	 * @param runDetached if must do
	 * @return This
	 */
	@Override
	@SuppressWarnings("unchecked")
	@NotNull
	public J setRunDetached(boolean runDetached)
	{
		this.runDetached = runDetached;
		return (J) this;
	}
	
	/**
	 * Getter for property 'requestId'.
	 *
	 * @return Value for property 'requestId'.
	 */
	@Override
	public boolean isRequestId()
	{
		return requestId;
	}
	
	/**
	 * Setter for property 'requestId'.
	 *
	 * @param requestId Value to set for property 'requestId'.
	 */
	@Override
	public void setRequestId(boolean requestId)
	{
		this.requestId = requestId;
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
	 * @param o of type Object
	 */
	@SuppressWarnings("unchecked")
	private <DB, JPA> void processId(DB o)
	{
		EntityAssistIDMapping<DB, JPA> mapping = EntityAssistBinder.lookup(o.getClass(), entity.getClassIDType());
		entity.setId((I) mapping.toObject(o));
	}
	
	/**
	 * Persist and Flush using the detached method (as a native query)
	 *
	 * @return This
	 */
	@Override
	@NotNull
	@SuppressWarnings({"unchecked", "Duplicates"})
	public J persistNow(E entity, boolean runDetached)
	{
		boolean transactionAlreadyStarted = false;
		ParsedPersistenceXmlDescriptor unit = GuiceContext.get(Key.get(ParsedPersistenceXmlDescriptor.class, getEntityManagerAnnotation()));
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
			{
				transactionAlreadyStarted = true;
				break;
			}
		}
		
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
		{
			if (!transactionAlreadyStarted && handler.active(unit))
			{
				handler.beginTransacation(false, getEntityManager(), unit);
			}
		}
		setRunDetached(runDetached);
		persist(entity);
		for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
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
	@Override
	@NotNull
	@SuppressWarnings({"Duplicates"})
	public E update(E entity) throws SQLException
	{
		try
		{
			if (onUpdate(entity))
			{
				if (isRunDetached())
				{
					String updateString = new UpdateStatement(entity).toString();
					log.log(defaultLoggingLevel, updateString);
					if (PersistenceServicesModule.getJtaConnectionBaseInfo()
					                             .containsKey(getEntityManagerAnnotation()))
					{
						DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
						if (!useDirectConnection || ds == null)
						{
							Query query = getEntityManager().createNativeQuery(updateString);
							query.executeUpdate();
						}
						else
						{
							var c = GuiceContext.get(Key.get(Connection.class, getEntityManagerAnnotation()));
							try (Statement st = c.createStatement())
							{
								st.executeUpdate(updateString);
							}
							if(!c.getAutoCommit() && commitDirectConnection)
							{
								c.commit();
							}
						}
					}
					else
					{
						Query query = getEntityManager().createNativeQuery(updateString);
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
	@Override
	@NotNull
	@SuppressWarnings({"Duplicates"})
	public E updateNow(E entity)
	{
		try
		{
			if (onUpdate(entity))
			{
				boolean transactionAlreadyStarted = false;
				ParsedPersistenceXmlDescriptor unit = GuiceContext.get(Key.get(ParsedPersistenceXmlDescriptor.class, getEntityManagerAnnotation()));
				for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
				{
					if (handler.active(unit) && handler.transactionExists(getEntityManager(), unit))
					{
						transactionAlreadyStarted = true;
						break;
					}
				}
				
				for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
				{
					if (!transactionAlreadyStarted && handler.active(unit))
					{
						handler.beginTransacation(false, getEntityManager(), unit);
					}
				}
				if (isRunDetached())
				{
					String updateString = new UpdateStatement(entity).toString();
					log.log(defaultLoggingLevel, updateString);
					if (PersistenceServicesModule.getJtaConnectionBaseInfo()
					                             .containsKey(getEntityManagerAnnotation()))
					{
						DataSource ds = GuiceContext.get(DataSource.class, getEntityManagerAnnotation());
						if (!useDirectConnection || ds == null)
						{
							Query query = getEntityManager().createNativeQuery(updateString);
							query.executeUpdate();
						}
						else
						{
							var c = GuiceContext.get(Key.get(Connection.class, getEntityManagerAnnotation()));
							try (Statement st = c.createStatement())
							{
								st.executeUpdate(updateString);
							}
							if(!c.getAutoCommit() && commitDirectConnection)
							{
								c.commit();
							}
						}
					}
					else
					{
						Query query = getEntityManager().createNativeQuery(updateString);
						query.executeUpdate();
					}
				}
				else
				{
					getEntityManager().merge(entity);
					getEntityManager().flush();
				}
				for (ITransactionHandler<?> handler : GuiceContext.get(ITransactionHandlerReader))
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
	 * @param entity The entity
	 * @return true if must carry on updating
	 */
	@Override
	public boolean onUpdate(E entity)
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
	@Override
	@NotNull
	public List<String> validateEntity(E entity)
	{
		List<String> errors = new ArrayList<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<?> constraintViolations = validator.validate(entity);
		if (!constraintViolations.isEmpty())
		{
			for (Object constraintViolation : constraintViolations)
			{
				ConstraintViolation<?> contraints = (ConstraintViolation<?>) constraintViolation;
				String error = contraints.getRootBeanClass()
				                         .getSimpleName() + STRING_DOT + contraints.getPropertyPath() + STRING_EMPTY + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}
	
	/**
	 * Returns the given attribute for a field name by reflectively accesing the static class
	 *
	 * @param fieldName the field to get an attribute for
	 * @return the attribute or null
	 */
	@Override
	public <X, Y> Attribute<X, Y> getAttribute(@NotNull String fieldName)
	{
		String clazz = getEntityClass().getCanonicalName() + CHAR_UNDERSCORE;
		try
		{
			Class<?> c = Class.forName(clazz);
			Field f = c.getField(fieldName);
			//noinspection unchecked
			return (Attribute<X, Y>) f.get(null);
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
	@Override
	public String getSelectIdentityString()
	{
		return selectIdentityString;
	}
	
	/**
	 * Method setSelectIdentityString sets the selectIdentityString of this QueryBuilderBase object.
	 *
	 * @param selectIdentityString the selectIdentityString of this QueryBuilderBase object.
	 */
	@Override
	public void setSelectIdentityString(String selectIdentityString)
	{
		this.selectIdentityString = selectIdentityString;
	}
	
	/**
	 * If a connection should be directly fetched from the datasource, or if an entity manager create native sql should be used
	 *
	 * @return
	 */
	public boolean isUseDirectConnection()
	{
		return useDirectConnection;
	}
	
	/**
	 * If a connection should be directly fetched from the datasource, or if an entity manager create native sql should be used
	 *
	 * @param useDirectConnection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setUseDirectConnection(boolean useDirectConnection)
	{
		this.useDirectConnection = useDirectConnection;
		return (J) this;
	}
	
	/**
	 * Commits the direct connection after execution
	 *
	 * @return
	 */
	public boolean isCommitDirectConnection()
	{
		return commitDirectConnection;
	}
	
	/**
	 * Commits the direct connection after execution
	 *
	 * @param commitDirectConnection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public QueryBuilderRoot<J, E, I> setCommitDirectConnection(boolean commitDirectConnection)
	{
		this.commitDirectConnection = commitDirectConnection;
		return (J) this;
	}
}
