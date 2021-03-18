package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.IQueryBuilder;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public interface IBaseEntity<J extends IBaseEntity<J, Q, I>, Q extends IQueryBuilder<Q, J, I>, I extends Serializable>
        extends IDefaultEntity<J, Q, I> {
    /**
     * Persists this object through the builder
     *
     * @return
     */
    @NotNull
    J persist();

    /**
     * Updates this object through the builder
     *
     * @return
     */
    @NotNull
    J update();

    /**
     * Persists this object through the builder
     *
     * @return
     */
    @NotNull
    J persistNow();

    /**
     * Deletes this entity with the entity mananger. This will remove the row.
     *
     * @return
     */
    @NotNull
    J delete();

    /**
     * Deletes this object from the ID
     *
     * @return
     */
    J deleteId();
}
