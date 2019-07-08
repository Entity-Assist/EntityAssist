package com.jwebmp.entityassist.querybuilder.statements;

import com.jwebmp.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.jwebmp.entityassist.entities.EntityClass;
import com.jwebmp.entityassist.entities.EntityClassGeneratedID;
import com.jwebmp.entityassist.enumerations.ActiveFlag;
import com.jwebmp.guicedpersistence.db.annotations.Transactional;
import com.jwebmp.logger.LogFactory;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

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