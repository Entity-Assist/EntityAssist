package com.entityassist;

import com.entityassist.querybuilder.QueryBuilder;
import com.google.inject.Key;
import com.guicedee.client.IGuiceContext;
import jakarta.persistence.EntityManager;

public class TransYtdQueryBuilder
        extends QueryBuilder<TransYtdQueryBuilder, TransYtd, TransYtdPK>
{

    public boolean onCreate(EntityClass entity)
    {
        return true;
    }


    @Override
    public EntityManager getEntityManager()
    {
        return IGuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
    }


    @Override
    public boolean isIdGenerated()
    {
        return false;
    }

    public boolean onUpdate(EntityClass entity)
    {
        return true;
    }
}
