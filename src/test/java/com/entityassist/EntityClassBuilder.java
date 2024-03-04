package com.entityassist;

import com.entityassist.querybuilder.QueryBuilderCore;
import com.google.inject.Key;
import com.guicedee.client.IGuiceContext;
import jakarta.persistence.EntityManager;

public class EntityClassBuilder
        extends QueryBuilderCore<EntityClassBuilder, EntityClass, Long>
{
    public EntityClassBuilder()
    {
    }

    @Override
    public EntityManager getEntityManager()
    {
        return IGuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
    }

    @Override
    public boolean onCreate(EntityClass entity)
    {
        return true;
    }

    @Override
    public boolean isIdGenerated()
    {
        return false;
    }

    @Override
    public boolean onUpdate(EntityClass entity)
    {
        return true;
    }
}
