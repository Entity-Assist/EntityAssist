package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.*;
import java.util.*;

public interface IBaseEntity<J extends IBaseEntity<J, Q, I>, Q extends IQueryBuilder<Q, J, I>, I extends Serializable>
        extends IDefaultEntity<J, Q, I> {
    /**
     * Persists this object through the builder
     *
     * @return
     */
    @NotNull
    J persist(@NotNull EntityManager entityManager);

    /**
     * Updates this object through the builder
     *
     * @return
     */
    @NotNull
    J update(@NotNull EntityManager entityManager);

    /**
     * Persists this object through the builder
     *
     * @return
     */
    @NotNull
    J persistNow(@NotNull EntityManager entityManager);

    /**
     * Deletes this entity with the entity mananger. This will remove the row.
     *
     * @return
     */
    @NotNull
    J delete(@NotNull EntityManager entityManager);
	
	
	Optional<J> find(I id, EntityManager entityManager);
	
	Optional<J> find(EntityManager entityManager);
	
	List<J> findAll(EntityManager entityManager);
}
