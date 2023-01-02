package com.entityassist;

import com.entityassist.querybuilder.*;
import com.entityassist.services.entities.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
        getterVisibility = NONE,
        setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class BaseEntity<J extends BaseEntity<J, Q, I>, Q extends QueryBuilder<Q, J, I>, I extends Serializable>
        extends DefaultEntity<J, Q, I>
        implements IBaseEntity<J, Q, I> {
    private static final Logger log = Logger.getLogger(BaseEntity.class.getName());

    /**
     * Constructs a new base entity type
     */
    public BaseEntity() {
        //No configuration needed
    }

    /**
     * Persists this object through the builder
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public J persist(@NotNull EntityManager entityManager) {
        builder(entityManager).persist((J) this);
        return (J) this;
    }

    /**
     * Updates this object through the builder
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public J update(@NotNull EntityManager entityManager) {
        try {
            builder(entityManager).update((J) this);
        } catch (SQLException e) {
            log.log(Level.WARNING, "Unable to update id : " + e, e);
        }
        return (J) this;
    }

    /**
     * Persists this object through the builder
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public J persistNow(@NotNull EntityManager entityManager) {
        builder(entityManager).persistNow((J) this);
        return (J) this;
    }

    /**
     * Deletes this entity with the entity mananger. This will remove the row.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public J delete(@NotNull EntityManager entityManager) {
        ((QueryBuilder) builder(entityManager))
                .delete(this);
        return (J) this;
    }
	
	/**
	 * Finds the entity with the given ID
	 *
	 * @param id The id to look for
	 * @return If it is found through a get method
	 */
	@Override
	public Optional<J> find(I id,EntityManager entityManager) {
		setId(id);
		return find(entityManager);
	}
	
	/**
	 * Finds this entity or refreshes
	 * @return If it is found through a get method
	 */
	@Override
	public Optional<J> find(EntityManager entityManager) {
		return builder(entityManager).find(getId())
		                             .get();
	}
	
	/**
	 * Finds all the entity types
	 *
	 * @return A list of get all from the current builder
	 */
	@Override
	public List<J> findAll(EntityManager entityManager) {
		return builder(entityManager).getAll();
	}
}
