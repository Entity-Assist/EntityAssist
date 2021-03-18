package com.entityassist.services.querybuilders;

import com.entityassist.enumerations.ActiveFlag;
import com.entityassist.services.entities.ICoreEntity;

import java.io.Serializable;

@SuppressWarnings("unused")
public interface IQueryBuilderCore<J extends IQueryBuilderCore<J, E, I>,
        E extends ICoreEntity<E, J, I>, I extends Serializable>
        extends IQueryBuilderSCD<J, E, I> {
    /**
     * Filters from the Active Flag suite where it is in the active range
     *
     * @return This
     */

    J inActiveRange();

    /**
     * Selects all records in the visible range
     *
     * @return This
     */

    J inVisibleRange();

    /**
     * Updates the current record with the given active flag type
     * uses the merge
     *
     * @param newActiveFlagType The new flag type to apply
     * @param entity            The entity to operate on
     * @return The entity
     */
    E delete(ActiveFlag newActiveFlagType, E entity);

    /**
     * Marks the record as archived updating the warehouse and effective to date timestamps
     *
     * @param entity The entity
     * @return The Entity
     */
    E archive(E entity);

    /**
     * Marks the given entity as the given status, with the effective to date and warehouse last updated as now
     * Merges the entity, then detaches,
     * <p>
     * Persists the new record down with the end of time used
     *
     * @param entity The entity
     * @param status The new status
     * @return The updated entity
     */
    E closeAndReturnNewlyUpdate(E entity, ActiveFlag status);
}
