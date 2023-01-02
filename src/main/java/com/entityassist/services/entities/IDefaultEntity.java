package com.entityassist.services.entities;

import com.entityassist.services.querybuilders.*;

import java.io.*;

public interface IDefaultEntity<J extends IDefaultEntity<J, Q, I>, Q extends IDefaultQueryBuilder<Q, J, I>, I extends Serializable>
        extends IRootEntity<J, Q, I> {

}
