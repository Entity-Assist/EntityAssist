package com.entityassist;

import com.entityassist.enumerations.*;
import com.entityassist.querybuilder.*;
import com.entityassist.services.entities.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.*;

import java.io.*;
import java.sql.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

/**
 * @param <J> Always this class (CRP)
 * @param <Q> The associated query builder class
 * @author GedMarc
 * @version 1.0
 * @since 06 Dec 2016
 */
@SuppressWarnings("unused")
@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
        getterVisibility = NONE,
        setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class CoreEntity<J extends CoreEntity<J, Q, I>, Q extends QueryBuilderCore<Q, J, I>, I extends Serializable>
        extends SCDEntity<J, Q, I>
        implements ICoreEntity<J, Q, I> {
    /**
     * A Row status identifier for a warehouse or OLAP system
     */
    @Basic(optional = false,
            fetch = FetchType.LAZY)
    @Column(nullable = false,
            name = "ActiveFlag",columnDefinition = "VARCHAR(25)",length = 25)
    @Enumerated(value = EnumType.STRING)
    @JdbcTypeCode(Types.VARCHAR)
    private ActiveFlag activeFlag;

    /**
     * Initialize the entity
     */
    public CoreEntity() {
        activeFlag = ActiveFlag.Active;
    }

    /**
     * Constructs with no parameters set, Great for search criteria
     *
     * @param blank constructs with nothing
     */
    public CoreEntity(@SuppressWarnings("unused") boolean blank) {
        //No Config
    }

    /**
     * Returns the active flag
     *
     * @return The associated active flag
     */
    public ActiveFlag getActiveFlag() {
        return activeFlag;
    }

    /**
     * Sets the active flag
     *
     * @param activeFlag The active flag
     * @return This
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public J setActiveFlag(ActiveFlag activeFlag) {
        this.activeFlag = activeFlag;
        return (J) this;
    }

    /**
     * Deletes this entity with the entity mananger. This will remove the row.
     *
     * @return This
     */
    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public J delete(EntityManager entityManager) {
        ((QueryBuilderCore) builder(entityManager))
                .delete(this);
        return (J) this;
    }
}
