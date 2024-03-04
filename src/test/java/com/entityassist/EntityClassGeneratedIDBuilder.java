package com.entityassist;

import com.entityassist.querybuilder.QueryBuilderCore;
import com.google.inject.Key;
import com.guicedee.client.IGuiceContext;
import jakarta.persistence.EntityManager;

public class EntityClassGeneratedIDBuilder
        extends QueryBuilderCore<EntityClassGeneratedIDBuilder, EntityClassGeneratedID, Long>
{
    public EntityClassGeneratedIDBuilder()
    {
        setSelectIdentityString("CALL SCOPE_IDENTITY();");
    }

    @Override
    public EntityManager getEntityManager()
    {
        return IGuiceContext.get(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
    }

    @Override
    public boolean isIdGenerated()
    {
        return true;
    }

}
