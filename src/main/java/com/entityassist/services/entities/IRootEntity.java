package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.IQueryBuilderRoot;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Map;

public interface IRootEntity<J extends IRootEntity<J, Q, I>, Q extends IQueryBuilderRoot<Q, J, I>, I extends Serializable> {
    /**
     * Returns the id of the given type in the generic decleration
     *
     * @return Returns the ID
     */
    @NotNull
    I getId();

    /**
     * Returns the id of the given type in the generic decleration
     *
     * @param id
     * @return
     */
    @NotNull
    J setId(I id);

    /**
     * Returns the builder associated with this entity
     *
     * @return The associated builder
     */
    @NotNull
    Q builder();

    /**
     * Any DB Transient Maps
     * <p>
     * Sets any custom properties for this core entity.
     * Dto Read only structure. Not for storage unless mapped as such in a sub-method
     *
     * @return
     */
    @NotNull
    Map<Serializable, Object> getProperties();
}
