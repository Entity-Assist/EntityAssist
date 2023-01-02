package com.entityassist.services.querybuilders;

import com.entityassist.services.entities.*;

import java.io.*;
import java.time.*;

@SuppressWarnings("unused")
public interface IQueryBuilderSCD<J extends IQueryBuilderSCD<J, E, I>,
        E extends ISCDEntity<E, J, I>,
        I extends Serializable>
        extends IQueryBuilder<J, E, I> {
    /**
     * Where effective from date is greater than today
     *
     * @return This
     */
    J inDateRange();

    /**
     * Returns the effective from and to date to be applied
     * <p>
     * Usually getDate()
     *
     * @param betweenThisDate The date
     * @return This
     */
    J inDateRange(LocalDateTime betweenThisDate);

    /**
     * Returns the effective from and to date to be applied when only the effective date is taken into consideration
     *
     * @param effectiveToDate The date
     * @return This
     */
    J inDateRange(LocalDateTime effectiveToDate, boolean toDate);

    /**
     * In date range from till now
     *
     * @param fromDate The date for from
     * @return This
     */
    J inDateRangeSpecified(LocalDateTime fromDate);

    /**
     * Specifies where effective from date greater and effective to date less than
     *
     * @param fromDate The from date
     * @param toDate   The to date
     * @return This
     */
    J inDateRange(LocalDateTime fromDate, LocalDateTime toDate);

    /**
     * Performs any required logic between the original and new entities during an update operation
     * which is a delete and marking of the record as historical, and the insert of a new record which is updated
     * <p>
     * The old and new entities may have the same id, the new entity id is emptied after this call for persistence.
     *
     * @param originalEntity The entity that is going to be deleted
     * @param newEntity      The entity that is going to be created
     * @return currently always true @TODO
     */
    boolean onDeleteUpdate(E originalEntity, E newEntity);
}
