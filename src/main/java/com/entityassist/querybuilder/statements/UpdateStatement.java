package com.entityassist.querybuilder.statements;

import com.entityassist.BaseEntity;
import com.entityassist.querybuilder.EntityAssistStrings;
import com.guicedee.guicedinjection.pairing.Pair;
import com.guicedee.logger.LogFactory;

import java.util.Map;
import java.util.logging.Logger;

public class UpdateStatement
		extends RunnableStatement
{
	private static final Logger log = LogFactory.getLog(UpdateStatement.class.getName());

	public UpdateStatement(BaseEntity<?,?,?> obj)
	{
		super(obj);
	}

	@SuppressWarnings("unchecked")
	public String buildUpdateStatement()
	{
		StringBuilder string = new StringBuilder();
		string.append("UPDATE ");

		string.append(getTableName())
		      .append(EntityAssistStrings.STRING_SPACE);
		string.append("SET ");

		Map<String,Object> updateMap = getObject().builder()
		                                                     .getUpdateFieldMap(getObject());

		for (Map.Entry<String, Object> entry : updateMap.entrySet())
		{
			Object value = entry.getValue();
			String valueString = getValue(value);
			string.append(entry.getKey());
			string.append(EntityAssistStrings.STRING_EQUALS_SPACE_EQUALS);
			string.append(valueString);
		}

		string = string.deleteCharAt(string.lastIndexOf(EntityAssistStrings.STRING_COMMNA));
		string.append(EntityAssistStrings.STRING_SPACE);
		string.append("WHERE ");
		Pair<String, Object> idPair = getIdPair();
		string.append(idPair.getKey() + EntityAssistStrings.STRING_EQUALS_SPACE_EQUALS + getValue(idPair.getValue()));
		string = string.deleteCharAt(string.lastIndexOf(EntityAssistStrings.STRING_COMMNA));

		return string.toString();
	}


	public String toString()
	{
		return buildUpdateStatement();
	}

}
