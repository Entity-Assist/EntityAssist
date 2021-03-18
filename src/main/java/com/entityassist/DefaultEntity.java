package com.entityassist;

import com.entityassist.querybuilder.builders.DefaultQueryBuilder;
import com.entityassist.services.entities.IDefaultEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = ANY,
        getterVisibility = NONE,
        setterVisibility = NONE)
@JsonInclude(NON_NULL)
public abstract class DefaultEntity<J extends DefaultEntity<J, Q, I>, Q extends DefaultQueryBuilder<Q, J, I>, I extends Serializable>
        extends RootEntity<J, Q, I>
        implements IDefaultEntity<J, Q, I> {
}
