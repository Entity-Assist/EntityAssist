package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.guicedpersistence.services.PropertiesEntityManagerReader;
import com.oracle.jaxb21.PersistenceUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LocalDateEntityManagerConvertorProperties
		implements PropertiesEntityManagerReader
{

	@Override
	public Map<String, String> processProperties(PersistenceUnit persistenceUnit, Properties props)
	{
		return new HashMap<>();
	}
}
