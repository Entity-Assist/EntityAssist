package com.entityassist.querybuilder.statements;

import com.entityassist.BaseEntity;
import com.guicedee.guicedinjection.pairing.Pair;
import com.guicedee.logger.LogFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static com.guicedee.guicedinjection.json.StaticStrings.*;

public class UpdateStatement
		extends RunnableStatement
{
	public UpdateStatement(BaseEntity<?, ?, ?> obj)
	{
		super(obj);
	}

	@SuppressWarnings("unchecked")
	public String buildUpdateStatement()
	{
		StringBuilder string = new StringBuilder();
		string.append(STRING_UPDATE_SQL);

		string.append(getTableName())
		      .append(STRING_SPACE);
		string.append(STRING_UPDATE_SET_SQL);

		Map<Field, Object> updateMap = getUpdateFieldMap(getObject());

		for (Map.Entry<Field, Object> entry : updateMap.entrySet())
		{
			Object value = entry.getValue();
			String valueString = value.toString();
			string.append(getColumnName(entry.getKey()));
			string.append(STRING_EQUALS_SPACE_EQUALS);
			string.append(valueString);
		}

		string.deleteCharAt(string.lastIndexOf(STRING_COMMNA));
		string.append(STRING_SPACE);
		string.append(STRING_WHERE_SQL);
		Pair<String, Object> idPair = getIdPair();
		boolean asString = idPair.getValue() instanceof String ||
		                   idPair.getValue() instanceof UUID;

		string.append(idPair.getKey())
		      .append(STRING_EQUALS_SPACE_EQUALS)
		      .append(asString ? "'" : STRING_EMPTY)
		      .append(idPair.getValue())
		      .append(asString ? "'" : STRING_EMPTY);
		//string.deleteCharAt(string.lastIndexOf(STRING_COMMNA));
		return string.toString();
	}

	public String toString()
	{
		return buildUpdateStatement();
	}

}
