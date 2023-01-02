package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.*;
import java.util.*;

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
    Q builder(EntityManager entityManager);
	
	String getTableName();
}
