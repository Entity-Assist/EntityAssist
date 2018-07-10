package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.guicedpersistence.db.PropertiesEntityManagerReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LocalDateEntityManagerConvertorProperties
		implements PropertiesEntityManagerReader
{

	@Override
	public Map<String, String> processProperties(Properties props)
	{
		return new HashMap<>();
	}
}
