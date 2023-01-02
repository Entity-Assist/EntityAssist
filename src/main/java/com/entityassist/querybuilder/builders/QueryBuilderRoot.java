package com.entityassist.querybuilder.builders;

import com.entityassist.*;
import com.entityassist.services.querybuilders.*;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.*;
import jakarta.validation.constraints.*;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.logging.*;


/**
 * Builds a Query Base
 *
 * @param <J> This class type
 * @param <E> The entity type
 * @param <I> The entity ID type
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public abstract class QueryBuilderRoot<J extends QueryBuilderRoot<J, E, I>, E extends RootEntity<E, J, I>, I extends Serializable> implements IQueryBuilderRoot<J, E, I>
{
	public static Level defaultLoggingLevel = Level.FINER;
	
	/**
	 * This logger
	 */
	private static final Logger log = Logger.getLogger("QueryBuilderRoot");
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
	 * The actual entity event
	 */
	private E entity;
	/**
	 * If the inserted ID should be request override
	 */
	private boolean requestId = true;
	
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
	 *
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
	 *
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
	 *
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
		persist(entity);
		getEntityManager().flush();
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
		return (Class<? extends Annotation>) em
				                                     .getProperties()
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
				getEntityManager().persist(entity);
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
	 *
	 * @return true if must still create
	 */
	@Override
	public boolean onCreate(E entity)
	{
		return true;
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
		persist(entity);
		getEntityManager().flush();
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
				getEntityManager().merge(entity);
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
				getEntityManager().merge(entity);
				getEntityManager().flush();
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
	 *
	 * @return true if must carry on updating
	 */
	@Override
	public boolean onUpdate(E entity)
	{
		return true;
	}
	
	/**
	 * Returns the given attribute for a field name by reflectively accesing the static class
	 *
	 * @param fieldName the field to get an attribute for
	 *
	 * @return the attribute or null
	 */
	@Override
	public <X, Y> Attribute<X, Y> getAttribute(@NotNull String fieldName)
	{
		String clazz = getEntityClass().getCanonicalName() + '_';
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
}
