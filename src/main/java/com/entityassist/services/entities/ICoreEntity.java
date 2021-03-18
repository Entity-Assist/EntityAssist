package com.entityassist.services.entities;

import com.entityassist.enumerations.ActiveFlag;
import com.entityassist.services.querybuilders.IQueryBuilderCore;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public interface ICoreEntity<J extends ICoreEntity<J, Q, I>, Q extends IQueryBuilderCore<Q, J, I>, I extends Serializable>
        extends ISCDEntity<J, Q, I> {

    ActiveFlag getActiveFlag();

    @NotNull J setActiveFlag(ActiveFlag activeFlag);
}
