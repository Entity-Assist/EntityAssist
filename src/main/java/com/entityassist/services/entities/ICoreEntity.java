package com.entityassist.services.entities;

import com.entityassist.enumerations.*;
import com.entityassist.services.querybuilders.*;
import jakarta.validation.constraints.*;

import java.io.*;

public interface ICoreEntity<J extends ICoreEntity<J, Q, I>, Q extends IQueryBuilderCore<Q, J, I>, I extends Serializable>
        extends ISCDEntity<J, Q, I> {

    ActiveFlag getActiveFlag();

    @NotNull J setActiveFlag(ActiveFlag activeFlag);
}
