package com.entityassist.services.querybuilders;

import com.entityassist.services.entities.*;
import jakarta.persistence.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IQueryBuilder<J extends IQueryBuilder<J, E, I>,
        E extends IBaseEntity<E, J, I>,
        I extends Serializable>
        extends IDefaultQueryBuilder<J, E, I> {
    /**
     * Trigger if select should happen
     *
     * @return if select should occur
     */
    boolean onSelect();

    /**
     * Trigger events on the query when selects occur
     */
    void onSelectExecution(TypedQuery<?> query);

    /**
     * Returns a long of the count for the given builder
     *
     * @return Long of results - generally never null
     */
    Long getCount();

    /**
     * Returns the generated query, always created new
     *
     * @param <T> Any type returned
     * @return A built typed query
     */
    <T> TypedQuery<T> getQuery();

    /**
     * Returns the query for a count, always created new
     *
     * @param <T> Any type returned
     * @return A built typed query
     */
    <T> TypedQuery<T> getQueryCount();
	
    /**
     * Returns the result set as a stream
     *
     * @param resultType The result type
     * @param <T>        The Class for the type to gerenify
     * @return A stream of the type
     */

    <T> Stream<T> getResultStream(Class<T> resultType);

    /**
     * Returns a non-distinct list and returns an empty optional if a non-unique-result exception is thrown
     *
     * @return An optional of the result
     */
    Optional<E> get();

    /**
     * Returns the first result returned
     *
     * @param returnFirst If the first should be returned in the instance of many results
     * @return Optional of the required object
     */
    Optional<E> get(boolean returnFirst);

    /**
     * Returns a list (distinct or not) and returns an empty optional if returns a list, or will simply return the first result found from
     * a list with the same criteria
     *
     * @return Optional of the given class type (which should be a select column)
     */

    <T> Optional<T> get(Class<T> asType);

    /**
     * If this builder is configured to return the first row
     *
     * @return If the first record must be returned
     */

    boolean isReturnFirst();

    /**
     * If a Non-Unique Exception is thrown re-run the query as a list and return the first item
     *
     * @param returnFirst if must return first
     * @return J
     */

    J setReturnFirst(boolean returnFirst);

    /**
     * Returns a list of entities from a distinct or non distinct list
     *
     * @return A list of entities returned
     */
    List<E> getAll();

    /**
     * Returns the list as the selected class type (for when specifying single select columns)
     *
     * @param returnClassType Returns a list of a given column
     * @param <T>             The type of the column returned
     * @return The type of the column returned
     */

    <T> List<T> getAll(Class<T> returnClassType);

    /**
     * Sets whether or not to detach the selected entity/ies
     *
     * @return This
     */
    J detach();

    /**
     * Returns the number of rows affected by the delete.
     * <p>
     * Bulk Delete Operation
     * <p>
     * WARNING : Be very careful if you haven't added a filter this will truncate the table or throw a unsupported exception if no filters.
     *
     * @return number of results deleted
     */
    int delete();
	
    /**
     * Deletes the given entity through the entity manager
     *
     * @param entity Deletes through the entity manager
     * @return This
     */
    E delete(E entity);

    /**
     * Returns the number of rows affected by the delete.
     * WARNING : Be very careful if you haven't added a filter this will truncate the table or throw a unsupported exception if no filters.
     *
     * @return The number of records deleted
     */
    int truncate();

    /**
     * If must be detached from the entity manager
     *
     * @return if the entity automatically detaches from the entity manager
     */
    boolean isDetach();

    /**
     * If must be detached from the entity manager
     *
     * @param detach if the entity must detach
     * @return this object
     */
    J setDetach(boolean detach);
}
