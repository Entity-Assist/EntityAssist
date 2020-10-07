package com.entityassist.services.querybuilders;

import com.entityassist.RootEntity;
import com.entityassist.querybuilder.builders.QueryBuilderRoot;

import javax.persistence.EntityManager;
import javax.persistence.Transient;
import javax.persistence.metamodel.Attribute;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IQueryBuilderRoot<J extends QueryBuilderRoot<J, E, I>,
		E extends RootEntity<E, J, I>,
		I extends Serializable>
{
	/**
	 * Returns the associated entity class for this builder
	 *
	 * @return class of type entity
	 */
	Class<E> getEntityClass();
	
	/**
	 * Returns a mapped entity on this builder
	 *
	 * @return the actual entity
	 */
	E getEntity();
	
	/**
	 * Sets the entity for this particular builder
	 *
	 * @param entity The entity
	 * @return J This object
	 */
	
	J setEntity(E entity);
	
	/**
	 * Returns the current set first results
	 *
	 * @return where to start the first results
	 */
	Integer getFirstResults();
	
	/**
	 * Sets the first restults to return
	 *
	 * @param firstResults the number of results to skip before loading
	 * @return This
	 */
	
	J setFirstResults(Integer firstResults);
	
	/**
	 * Returns the current set maximum results
	 *
	 * @return int
	 */
	Integer getMaxResults();
	
	/**
	 * Sets the maximum results to return
	 *
	 * @param maxResults the maximum number of results to return
	 * @return This
	 */
	
	J setMaxResults(Integer maxResults);
	
	/**
	 * Persist and Flush
	 * <p>
	 * doesn't set run detached - executes flush after persist
	 *
	 * @return This
	 */
	J persistNow(E entity);
	
	/**
	 * Returns the annotation associated with the entity manager
	 *
	 * @return The annotations associated with this builder
	 */
	
	Class<? extends Annotation> getEntityManagerAnnotation();
	
	/**
	 * Returns the assigned entity manager
	 *
	 * @return The entity manager to use for this run
	 */
	EntityManager getEntityManager();
	
	/**
	 * Persists this entity. Uses the get instance entity manager to operate.
	 *
	 * @return This
	 */
	
	J persist(E entity);
	
	/**
	 * Performed on create/persist
	 *
	 * @param entity The entity
	 * @return true if must still create
	 */
	boolean onCreate(E entity);
	
	/**
	 * If this entity should run in a detached and separate to the entity manager
	 * <p>
	 * If the library generates the sql and runs it through a native query. Use InsertStatement, SelectStatement, Delete and UpdateStatement to view the queries that will get run
	 *
	 * @return boolean
	 */
	boolean isRunDetached();
	
	/**
	 * If this entity should run in a detached and separate to the entity manager
	 * <p>
	 * If the library generates the sql and runs it through a native query. Use InsertStatement, SelectStatement, Delete and UpdateStatement to view the queries that will get run
	 *
	 * @param runDetached if must do
	 * @return This
	 */
	
	J setRunDetached(boolean runDetached);
	
	/**
	 * If this ID is generated from the source and which form to use
	 * Default is Generated
	 *
	 * @return Returns if the id column is a generated type
	 */
	boolean isIdGenerated();
	
	/**
	 * Getter for property 'requestId'.
	 *
	 * @return Value for property 'requestId'.
	 */
	boolean isRequestId();
	
	/**
	 * Setter for property 'requestId'.
	 *
	 * @param requestId Value to set for property 'requestId'.
	 */
	void setRequestId(boolean requestId);
	
	/**
	 * Persist and Flush using the detached method (as a native query)
	 *
	 * @return This
	 */
	J persistNow(E entity, boolean runDetached);
	
	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return This
	 */
	E update(E entity) throws SQLException;
	
	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return This
	 */
	E updateNow(E entity);
	
	/**
	 * Performed on update/persist
	 *
	 * @param entity The entity
	 * @return true if must carry on updating
	 */
	boolean onUpdate(E entity);
	
	/**
	 * Performs the constraint validation and returns a list of all constraint errors.
	 *
	 * <b>Great for form checking</b>
	 *
	 * @return List of Strings
	 */
	List<String> validateEntity(E entity);
	
	/**
	 * Returns the given attribute for a field name by reflectively accesing the static class
	 *
	 * @param fieldName the field to get an attribute for
	 * @return the attribute or null
	 */
	<X, Y> Attribute<X, Y> getAttribute(String fieldName);
	
	/**
	 * Method getSelectIdentityString returns the selectIdentityString of this QueryBuilderBase object.
	 *
	 * @return the selectIdentityString (type String) of this QueryBuilderBase object.
	 */
	String getSelectIdentityString();
	
	/**
	 * Method setSelectIdentityString sets the selectIdentityString of this QueryBuilderBase object.
	 *
	 * @param selectIdentityString the selectIdentityString of this QueryBuilderBase object.
	 */
	void setSelectIdentityString(String selectIdentityString);
}
