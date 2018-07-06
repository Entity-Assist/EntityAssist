package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.guicedpersistence.db.PropertiesEntityManagerReader;

import java.util.HashMap;
import java.util.Map;

public class LocalDateEntityManagerConvertorProperties
		implements PropertiesEntityManagerReader
{

	@Override
	public Map<String, String> processProperties()
	{
		return new HashMap<>();
	}
}
