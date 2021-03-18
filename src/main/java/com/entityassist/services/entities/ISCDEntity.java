package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.IQueryBuilderSCD;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface ISCDEntity<J extends ISCDEntity<J, Q, I>, Q extends IQueryBuilderSCD<Q, J, I>, I extends Serializable>
        extends IBaseEntity<J, Q, I> {
    /**
     * Returns the effective from date for the given setting
     *
     * @return
     */
    LocalDateTime getEffectiveFromDate();
    /**
     * Sets the effective from date value for default value
     *
     * @param effectiveFromDate
     *
     * @return
     */
    @NotNull
    J setEffectiveFromDate(@NotNull LocalDateTime effectiveFromDate);

    /**
     * Returns the effice to date setting for active flag calculation
     *
     * @return
     */
    LocalDateTime getEffectiveToDate();

    /**
     * Sets the effective to date column value for active flag determination
     *
     * @param effectiveToDate
     * @return This
     */
    @NotNull
    J setEffectiveToDate(@NotNull LocalDateTime effectiveToDate);

    /**
     * Returns the warehouse created timestamp column value
     *
     * @return The current time
     */
    LocalDateTime getWarehouseCreatedTimestamp();

    /**
     * Sets the warehouse created timestamp
     *
     * @param warehouseCreatedTimestamp The time to apply
     * @return This
     */
    @NotNull
    J setWarehouseCreatedTimestamp(@NotNull LocalDateTime warehouseCreatedTimestamp);


    /**
     * Returns the last time the warehouse timestamp column was updated
     *
     * @return The time
     */
    LocalDateTime getWarehouseLastUpdatedTimestamp();

    /**
     * Sets the last time the warehouse timestamp column was updated
     *
     * @param warehouseLastUpdatedTimestamp
     *
     * @return This
     */
    @NotNull
    J setWarehouseLastUpdatedTimestamp(@NotNull LocalDateTime warehouseLastUpdatedTimestamp);
}
