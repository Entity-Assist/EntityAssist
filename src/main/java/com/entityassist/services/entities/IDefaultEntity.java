package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.IDefaultQueryBuilder;

import java.io.Serializable;

public interface IDefaultEntity<J extends IDefaultEntity<J, Q, I>, Q extends IDefaultQueryBuilder<Q, J, I>, I extends Serializable>
        extends IRootEntity<J, Q, I> {

}
