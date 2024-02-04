package com.entityassist.services.querybuilders;

import com.entityassist.services.entities.IRootEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.List;

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
     * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
     *
     * @return This
     */
    E update(E entity) throws SQLException;

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
