package za.co.mmagon.entityassist;

import za.co.mmagon.guiceinjection.db.connectionbasebuilders.H2DefaultConnectionBaseBuilder;

import java.lang.annotation.Annotation;

public class EntityAssistTestDBModule extends H2DefaultConnectionBaseBuilder
{

	@Override
	protected String getJndiMapping()
	{
		return "jdbc/eatest";
	}

	@Override
	protected String getPersistenceUnitName()
	{
		return "h2entityAssist";
	}

	@Override
	protected Class<? extends Annotation> getBindingAnnotation()
	{
		return TestEntityAssistCustomPersistenceLoader.class;
	}
}
