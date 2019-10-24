package com.guicedee.entityassist.querybuilder.statements;

import com.guicedee.entityassist.enumerations.ActiveFlag;
import com.guicedee.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.guicedee.entityassist.entities.EntityClass;
import com.guicedee.entityassist.entities.EntityClassGeneratedID;
import com.guicedee.guicedpersistence.db.annotations.Transactional;
import com.guicedee.logger.LogFactory;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;

class UpdateStatementTest
{

	@Test
	@Transactional(entityManagerAnnotation = TestEntityAssistCustomPersistenceLoader.class)
	void buildUpdateStatement()
	{
		LogFactory.setDefaultLevel(Level.FINE);
		UpdateStatement updateStatement = new UpdateStatement(new EntityClass().setActiveFlag(ActiveFlag.Always).setId(1L));
		System.out.println(updateStatement.buildUpdateStatement());
		EntityClassGeneratedID generatedID = new EntityClassGeneratedID();
		generatedID.builder()
		           .setRunDetached(true)
		           .persistNow(generatedID);

		updateStatement = new UpdateStatement(generatedID);
		System.out.println(updateStatement);

		generatedID.builder().setRunDetached(true).update(generatedID);
		generatedID.builder().setRunDetached(true).updateNow(generatedID);

	}
}