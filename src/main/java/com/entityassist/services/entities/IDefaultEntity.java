package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.IDefaultQueryBuilder;
import com.google.common.base.Strings;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

public interface IDefaultEntity<J extends IDefaultEntity<J, Q, I>, Q extends IDefaultQueryBuilder<Q, J, I>, I extends Serializable>
        extends IRootEntity<J, Q, I> {
	
	
}
