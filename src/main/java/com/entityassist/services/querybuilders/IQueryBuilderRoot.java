package com.entityassist.services.querybuilders;

import com.entityassist.services.entities.*;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.*;

import java.io.*;
import java.lang.annotation.*;
import java.sql.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IQueryBuilderRoot<J extends IQueryBuilderRoot<J, E, I>,
        E extends IRootEntity<E, J, I>,
        I extends Serializable> {
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
	 * Sets this builders entity manager
	 * @param entityManager
	 *
	 * @return
	 */
	J setEntityManager(EntityManager entityManager);

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
     * Returns the given attribute for a field name by reflectively accesing the static class
     *
     * @param fieldName the field to get an attribute for
     * @return the attribute or null
     */
    <X, Y> Attribute<X, Y> getAttribute(String fieldName);
}
