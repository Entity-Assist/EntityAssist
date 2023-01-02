package com.entityassist;

import com.entityassist.querybuilder.builders.*;
import com.entityassist.services.entities.*;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.io.*;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.*;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
        getterVisibility = NONE,
        setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class DefaultEntity<J extends DefaultEntity<J, Q, I>, Q extends DefaultQueryBuilder<Q, J, I>, I extends Serializable>
        extends RootEntity<J, Q, I>
        implements IDefaultEntity<J, Q, I> {
}
